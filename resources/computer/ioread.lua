--[[
    ioread.lua is a module of cosmOS, a fictional operating system.
    Copyright (C) 2015 Cel Skeggs

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
