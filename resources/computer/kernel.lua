_KVERSION = "cosmOS 0.1.0"
local function split_lines(str, cb)
    local last_end = 1
    local s, e, cap = str:find("(.-)\n", 1)
    while s do
        if s ~= 1 or cap ~= "" then
            cb(cap)
        end
        last_end = e+1
        s, e, cap = str:find("(.-)\n", last_end)
    end
    if last_end <= #str then
        cap = str:sub(last_end)
        cb(cap)
    end
end
local _, _, SCRDIM = query_info()
local LINES = tonumber(SCRDIM:sub(SCRDIM:find("x") + 1)) or 10
local current_input_line = ""
function set_input_line(input)
    current_input_line = tostring(input)
    post("put_line", LINES - 1, current_input_line)
end
function putstr(x)
    post("scroll_line", -1)
    post("put_line", LINES - 2, tostring(x))
    post("put_line", LINES - 1, current_input_line)
end
function putstrl(x)
    split_lines(x, putstr)
end
function print(...)
    local out = {...}
    local n = select("#", ...)
    for i=1, n do
        out[i] = tostring(out[i])
    end
    putstrl(table.concat(out, " ", 1, n))
end

local active_tasks = {}
local tasks = {}
local add_tasks = {}
local del_tasks = {}
local next_pid = 1
local active_pid = nil
local subscribers = {}
local each_tick = {}
local event_authorities = {}
function activate_task(pid)
    assert(type(pid) == "number")
    if tasks[pid] then
        active_tasks[pid] = true
        return true
    else
        return false
    end
end
function add_task(base)
    assert(type(base) == "function")
    local pid = next_pid
    next_pid = next_pid + 1
    add_tasks[pid] = coroutine.create(base)
    return pid
end
function kill(pid)
    del_tasks[pid] = tasks[pid]
end
function is_running(pid)
    return add_tasks[pid] ~= nil or tasks[pid] ~= nil
end
function get_pid()
    return active_pid
end
function block()
    coroutine.yield("deactivate")
end
function set_is_ticker(receive)
    assert(type(receive) == "boolean")
    if receive then
        each_tick[get_pid()] = true
    else
        each_tick[get_pid()] = nil
    end
end
local function post_event(is_dev, name, ...)
    if not name then return false end
    if is_dev then name = "dev_" .. name end
    local subs = subscribers[name]
    local any = false
    if subs then
        for pid, queue in pairs(subs) do
            if activate_task(pid) then
                table.insert(queue, {...})
                any = true
            else -- task doesn't exist - clear its queue
                subs[pid] = nil
            end
        end
    end
    return is_dev or any
end
function become_authority(name)
    assert(type(name) == "string")
    assert(name:sub(1, 4) ~= "dev_" and name:sub(1, 7) ~= "kernel_", "Authority already exists for event: " .. name)
    if event_authorities[name] ~= nil and tasks[event_authorities[name]] == nil then
        event_authorities[name] = nil
    end
    assert(event_authorities[name] == nil, "Authority already exists for event: " .. name)
    event_authorities[name] = get_pid()
    return function(...)
        return post_event(false, name, ...)
    end
end
function subscribe_event(event)
    assert(type(event) == "string")
    local pid = get_pid()
    if not subscribers[event] then subscribers[event] = {} end
    if not subscribers[event][pid] then subscribers[event][pid] = {} end
    local queue = subscribers[event][pid]
    return function()
        if queue[1] then
            return true, unpack(table.remove(queue, 1))
        else
            return false
        end
    end
end
function unsubscribe_event(event)
    assert(type(event) == "string")
    local pid = get_pid()
    if pid and subscribers[event] and subscribers[event][pid] then
        subscribers[event][pid] = nil
        return true
    else
        return false
    end
end
function mainloop()
    local deadlocked = true
    for pid, v in pairs(each_tick) do
        if not v or not activate_task(pid) then
            each_tick[pid] = nil
        end
    end
    while true do
        for pid, task in pairs(add_tasks) do
            assert(tasks[pid] == nil)
            tasks[pid] = task
            active_tasks[pid] = true
            add_tasks[pid] = nil
            post_event(false, "kernel_new_task", pid)
        end
        for pid, task in pairs(del_tasks) do
            assert(tasks[pid] == task)
            tasks[pid] = nil
            active_tasks[pid] = nil
            del_tasks[pid] = nil
            post_event(false, "kernel_del_task", pid)
        end

        while true do
            if not post_event(true, poll()) then break end
        end

        local any = false
        for pid, task in pairs(tasks) do
            if active_tasks[pid] then
                any = true
                assert(coroutine.status(task) == "suspended")
                active_pid = pid
                local success, err = coroutine.resume(task)
                if not success then
                    putstr("[DEBUG] task " .. pid .. " terminated: " .. tostring(err))
                    del_tasks[pid] = task
                elseif coroutine.status(task) == "dead" then -- normal exit
                    del_tasks[pid] = task
                else
                    if err == "deactivate" then
                        active_tasks[pid] = nil
                    end
                end
                active_pid = nil -- don't set it until here so that any of the tostrings above won't cause issues
            end
        end
        if not any then
            if not deadlocked then return false end
            for event_type, sub_list in pairs(subscribers) do
                for pid, queue in pairs(sub_list) do
                    if tasks[pid] then
                        return false
                    end
                end
            end
            return true -- DEADLOCKED
        end
        deadlocked = false
    end
end
-- userspace utility functions
function net_id()
    local systype, netid, scrdim = query_info()
    return netid
end
function net_send(tid, data)
    post("net_send", tid, data)
end
function net_recv()
    local hndl = subscribe_event("dev_net_recv")
    while true do
        local found, src, data, result = hndl()
        if found then
            assert(unsubscribe_event("dev_net_recv"))
            return src, data, result
        else
            block()
        end
    end
end
function list_files()
    local hndl = subscribe_event("dev_disk_list")
    post("disk_list")
    while true do
        local got, data = hndl()
        if got then
            unsubscribe_event("dev_disk_list")
            local out = {}
            split_lines(data, function(fname) table.insert(out, fname) end)
            return out
        else
            block()
        end
    end
end
function load_file(fname)
    local hndl = subscribe_event("dev_disk_read")
    post("disk_read", fname)
    while true do
        local got, gotname, data = hndl()
        if got and gotname == fname then
            unsubscribe_event("dev_disk_read")
            return data
        else
            block()
        end
    end
end
function load_file_lines(fname, cb)
    if not cb then
        local out = {}
        if not load_file_lines(fname, function(x) table.insert(out, x) end) then
            return nil, "nonexistent"
        else
            return out
        end
    end
    local data = load_file(fname)
    if not data then return false end
    split_lines(data, cb)
    return true
end
function save_file(fname, data)
    post("disk_write", fname, data)
    coroutine.yield()
end
function save_file_lines(fname, lines)
    if lines[1] then
        save_file(fname, table.concat(lines, "\n") .. "\n")
    else
        save_file(fname, "")
    end
end
function add_task_disk(fname, ...)
    local data = load_file(fname)
    if not data then
        error("Could not load file: " .. fname)
    end
    local f, err = loadstring(data, fname) -- TODO: sandbox this
    if not f then
        error("Could not load " .. fname .. ": " .. tostring(err))
    end
    if select("#", ...) > 0 then
        local t, n = {...}, select("#", ...)
        return add_task(function() f(unpack(t, 1, n)) end)
    else
        return add_task(f)
    end
end
function run_file(fname, ...)
    local subs = subscribe_event("kernel_del_task")
    local pid = add_task_disk(fname, ...)
    while true do
        if is_running(pid) then
            subs()
            block()
        else
            unsubscribe_event("kernel_del_task")
            break
        end
    end
end
-- commands
function echo(...)
    print(...)
end
function edit(name)
    run_file("editor.lua", name)
end
-- setup task
add_task(function()
    -- io reader task
    add_task_disk("ioread.lua")
    -- lua interpreter
    add_task_disk("lua.lua")
end)
putstr("Welcome to " .. _KVERSION .. " on " .. query_info())
putstr("  " .. _VERSION)
return function()
    local succ, err, e2, e3 = pcall(mainloop)
    if not succ then
        putstrl("FAILED TO RUN: " .. tostring(err) .. " " .. tostring(e2) .. " " .. tostring(e3))
        error("crash")
    elseif err then
        putstr("DEADLOCKED")
        error("crash")
    end
end
