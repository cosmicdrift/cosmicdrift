--[[
    editor.lua is a module of cosmOS, a fictional operating system.
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

local path = ...
local lines
local function fed_load(path)
    local lnl, err = load_file_lines(path)
    if not lnl then
        error(err)
    end
    lines = lnl
    print("loaded: " .. path .. ": " .. #lines .. " lines.")
end
local function fed_save(path)
    save_file_lines(path, lines)
    print("saved: " .. path .. ": " .. #lines .. " lines.")
end
if path then
    fed_load(path)
else
    lines = {}
    print("empty buffer")
end
local running = true
local function fed_cmd(c, argfrom, argto, arg2)
    if c == "i" then
        if argfrom then
            for line = argfrom, argto do
                table.insert(lines, line, arg2)
            end
        else
            table.insert(lines, arg2)
        end
    elseif c == "e" then
        if argfrom then
            assert(argfrom >= 1 and argfrom <= #lines, "line out of bounds: " .. argfrom)
            assert(argto >= 1 and argto <= #lines, "line out of bounds: " .. argto)
            for line = argfrom, argto do
                lines[line] = arg2
            end
        else
            assert(#lines ~= 0, "empty file")
            lines[#lines] = arg2
        end
    elseif c == "p" then
        if argfrom then
            assert(argfrom >= 1 and argfrom <= #lines, "line out of bounds: " .. argfrom)
            assert(argto >= 1 and argto <= #lines, "line out of bounds: " .. argto)
            for line = argfrom, argto do
                print(line .. ": " .. lines[line])
            end
        else
            print("lines: " .. #lines)
            for i, v in ipairs(lines) do
                print(i .. ": " .. v)
            end
        end
    elseif c == "d" then
        assert(argfrom, "expected a line")
        assert(argfrom >= 1 and argfrom <= #lines, "line out of bounds: " .. argfrom)
        assert(argto >= 1 and argto <= #lines, "line out of bounds: " .. argto)
        for line = argto, argfrom, -1 do
            table.remove(lines, line)
        end
    elseif c == "w" then
        if #arg2 > 0 then
            fed_save(arg2)
        else
            assert(path, "expected a file")
            fed_save(path)
        end
    elseif c == "r" then
        if #arg2 > 0 then
            fed_load(arg2)
        else
            assert(path, "expected a file")
            fed_load(path)
        end
    elseif c == "q" then
        print("bye")
        running = false
    elseif c == "c" then
        print("line count: " .. #lines)
    elseif c == "h" then
        print("fed help: file editor")
        print("<CMD><ARG1> <ARG2>")
        print("ARG1 can be a line number or a range A-B")
        print("Commands:")
        print("i<L> <TEXT>: insert a line before line L (default: end)")
        print("e<L> <TEXT>: replace line L (default: end)")
        print("p<L>: print line L (default: all)")
        print("d<L>: delete line L (default: do nothing)")
        print("w <FILE>: write the buffer to FILE (default: command-line file)")
        print("r <FILE>: read the buffer from FILE (default: command-line file)")
        print("c: count the number of lines")
        print("q: quit fed")
        print("h: show this help")
    else
        print("unknown fed command (try 'h')")
    end
end
local get_line = subscribe_event("input_line")
while running do
    local got, cmd = get_line()
    if got then
        print("fed> " .. cmd)
        local c = cmd:sub(1, 1)
        local spt = cmd:find(" ", 2)
        local arg1, arg2
        local valid = true
        if spt then
            arg1, arg2 = cmd:sub(2, spt - 1), cmd:sub(spt + 1)
        else
            arg1, arg2 = cmd:sub(2), ""
        end
        local argfrom, argto = tonumber(arg1), nil
        if not argfrom and arg1:find("-") then
            local spt = arg1:find("-")
            argfrom, argto = tonumber(arg1:sub(1, spt - 1)), tonumber(arg1:sub(spt + 1))
            if not argfrom or not argto or argto < argfrom then
                print("invalid range")
                valid = false
            end
        else
            argto = argfrom
        end
        if valid then
            local succ, err = pcall(fed_cmd, c, argfrom, argto, arg2)
            if not succ then
                print("failed: " .. err)
            end
        end
    else
        block()
    end
end
