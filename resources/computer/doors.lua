--[[
    doors.lua is a module of cosmOS, a fictional operating system.
    Copyright (C) 2015 Colby Skeggs

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
]]

local get_line = subscribe_event("input_line")
local function scan()
    print("scanning door network...")
    local get_msg = subscribe_event("dev_net_recv")
    net_send(0, "ping")
    set_is_ticker(true)
    for i=1,4 do
        block()
    end
    set_is_ticker(false)
    local doors = {}
    while true do
        local got, src, msg, tgt = get_msg()
        if not got then break end
        if tgt ~= 0 then
            local expect = "pong-door-" .. src .. "-"
            if msg:sub(1, #expect) == expect then
                local state = msg:sub(#expect + 1)
                if state == "disabled" then
                    doors[src] = true
                else
                    if state ~= "enabled" then
                        print("Unknown status of door: " .. msg)
                    end
                    doors[src] = false
                end
            end
        end
    end
    local count = 0
    for k, v in pairs(doors) do
        count = count + 1
    end
    print("door network scanned:", count, "doors:")
    for k, v in pairs(doors) do
        if v then
            print("door", k, "open")
        else
            print("door", k, "closed")
        end
    end
end
local function open(id)
    local num = tonumber(id)
    if not num or num % 1 ~= 0 or num < 0 or num >= 65536 then
        print("invalid door ID: " .. id)
    else
        net_send(num, "disable")
        print("door told to open: " .. num)
    end
end
local function close(id)
    local num = tonumber(id)
    if not num or num % 1 ~= 0 or num < 0 or num >= 65536 then
        print("invalid door ID: " .. id)
    else
        net_send(num, "enable")
        print("door told to close: " .. num)
    end
end
local function toggle(id)
    local num = tonumber(id)
    if not num or num % 1 ~= 0 or num < 0 or num >= 65536 then
        print("invalid door ID: " .. id)
    else
        net_send(num, "toggle")
        print("door told to toggle: " .. num)
    end
end
scan()
print("door control system ready")
while true do
    local got, line = get_line()
    if got then
        print("doors>", line)
        local s = line:find(" ")
        local cmd, arg
        if s then
            cmd, arg = line:sub(1, s - 1), line:sub(s + 1)
        else
            cmd, arg = line, ""
        end
        if cmd == "scan" then
            scan()
        elseif cmd == "open" then
            open(arg)
        elseif cmd == "close" then
            close(arg)
        elseif cmd == "toggle" then
            toggle(arg)
        elseif cmd == "quit" then
            break
        elseif cmd == "help" then
            print("scan: scan network for doors")
            print("toggle <ID>: toggle door")
            print("open <ID>: open door")
            print("close <ID>: close door")
            print("help: show this listing")
            print("quit: exit this program")
        else
            print("unknown command: " .. cmd .. ": try 'help'.")
        end
    else
        block()
    end
end
