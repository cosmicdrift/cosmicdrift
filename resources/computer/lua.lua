local get_line = subscribe_event("input_line")
local function pack(...)
    return {...}, select('#', ...)
end
print("System Ready")
while true do
    local has, line = get_line()
    if has then
        putstr("$ " .. line)
        if line:sub(1, 1) == "=" then
            line = "return " .. line:sub(2)
        end
        local closure, err = loadstring(line)
        if not closure then
            putstr("Bad syntax: " .. err)
        else
            local res, n = pack(pcall(closure))
            if res[1] then
                if n > 1 then
                    for i = 2, n do
                        local value = res[i]
                        res[i] = tostring(value)
                        if type(value) == "table" and res[i]:sub(1, 8) == "table 0x" then
                            local out = "{"
                            for i,v in ipairs(value) do
                                out = out .. tostring(v) .. ", "
                            end
                            for k,v in pairs(value) do
                                if type(k) ~= "number" or k % 1 ~= 0 then
                                    out = out .. tostring(k) .. "=" .. tostring(v) .. ", "
                                end
                            end
                            res[i] = out:sub(1, #out - 2) .. "}"
                        end
                    end
                    putstrl(table.concat(res, " ", 2, n))
                end
            else
                putstr('Failed: ' .. res[2])
                if #res[3] > 0 then
                    putstrl(res[3])
                end
            end
            while get_line() do end -- get rid of anything received while the line was running
        end
    else
        block()
    end
end
