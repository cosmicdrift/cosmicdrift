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
