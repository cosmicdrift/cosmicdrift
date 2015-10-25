local post_line = become_authority("input_line")
local get_key = subscribe_event("dev_key_press")
local line = ""
local modified = true
while true do
    local has, key = get_key()
    if has then
        assert(has == true)
        if key >= 32 and key <= 126 then
            line = line .. string.char(key)
            modified = true
        elseif key == 10 then
            if not post_line(line) then
                putstr("Line not accepted: " .. line)
            end
            line = ""
            modified = true
        elseif key == 8 then
            if #line > 0 then
                line = line:sub(1, #line - 1)
                modified = true
            end
        else
            putstr("Unhandled key: " .. tostring(key))
        end
    else
        if modified then
            set_input_line("> " .. line)
            modified = false
        end
        block()
    end
end