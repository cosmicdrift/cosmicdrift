--[[
    bios.lua is a module of cosmOS, a fictional operating system.
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

if _kernel_code ~= nil then
    post("put_line", 1, "KERNEL ALREADY LOADED.")
    error("crash")
end
while true do
    local event, fname, data = poll()
    if not event then break end
    if event == "disk_read" and fname == "kernel.lua" then
        if data == nil then
            post("put_line", 1, "KERNEL NOT FOUND.")
            error("crash")
        end
        local kc, err = loadstring(data, fname)
        if not kc then
            post("put_line", 1, "KERNEL NOT LOADED.")
            post("put_line", 2, tostring(err))
            error("crash")
        end
        kc, _kernel_code, e2 = pcall(kc)
        if not kc then
            post("put_line", 1, "KERNEL NOT STARTED: " .. tostring(_kernel_code))
            post("put_line", 2, tostring(e2))
            error("crash")
        elseif not _kernel_code then
            post("put_line", 1, "KERNEL PROVIDED NIL")
            error("crash")
        end
        kexec(_kernel_code)
    end
end
if _kernel_code == nil then
    post("put_line", 0, "LOADING KERNEL...")
    post("disk_read", "kernel.lua")
end
