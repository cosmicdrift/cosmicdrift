package io.github.cosmicdrift.cosmicdrift.computer;

import io.github.cosmicdrift.cosmicdrift.networks.Packet;
import java.io.UnsupportedEncodingException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

public class BinComputerCore {

    private static final boolean DEBUG = false;
    private final ByteBuffer data = ByteBuffer.allocate(20480); // IP stored as offset in data.
    private final int[] execs = new int[20480];
    private int dataStk = 15360, retStk = 15872;
    public final int maxBuffer = 256;
    public final LinkedList<Packet> received = new LinkedList<>();
    public final LinkedList<Packet> sending = new LinkedList<>();
    public byte[] disk;
    private boolean crashed = false; // TODO: Better crashing.

    public BinComputerCore(byte[] bootstrap, byte[] disk) {
        data.put(bootstrap);
        data.position(0);
        if (disk == null) {
            disk = new byte[32768];
        } else if (disk.length != 32768) {
            throw new IllegalArgumentException("Bad disk size: " + disk.length);
        }
        this.disk = disk;
    }

    public void setNetworkAddress(short address) {
        data.putShort(18432, address);
    }

    public void keyPress(byte key) {
        int len = data.getShort(19456) % 1022;
        data.put(19456 + 2 + len, key);
        data.putShort(19456, (short) (len + 1));
    }

    private void keyShift() {
        data.putShort(19456, (short) ((data.getShort(19456) - 1) % 1022));
        for (int i = 2; i < 1023; i++) {
            data.put(19456 + i, data.get(19456 + i + 1));
        }
    }

    private void keyClear() {
        data.putShort(19456, (short) 0);
    }

    private void netNext() {
        if (received.isEmpty()) {
            data.putShort(18434, (short) 0);
        } else {
            Packet pkt = received.removeFirst();
            short id = pkt.targetID;
            short me = data.getShort(18432);
            if (id != 0 && id != me) {
                System.out.println("Dropped message: " + id + " instead of " + me);
                return;
            }
            data.putShort(18434, pkt.sourceID);
            int i;
            for (i = 0; i < 508 && i < pkt.data.length; i++) {
                data.put(18436 + i, pkt.data[i]);
            }
            for (; i < 508; i++) {
                data.put(18436 + i, (byte) 0);
            }
        }
    }

    private void diskLoad(int i) {
        System.arraycopy(disk, i * 1024, data.array(), 17408, 1024);
    }

    private void diskSave(int i) {
        System.arraycopy(data.array(), 17408, disk, i * 1024, 1024);
    }

    private void netSend() {
        byte[] cur = new byte[508];
        for (int i = 0; i < cur.length; i++) {
            cur[i] = data.get(18948 + i);
        }
        sending.addLast(new Packet(cur, data.getShort(18432), data.getShort(18946)));
    }

    public String[] screen = new String[16];

    private void push(int i) {
        data.putInt(dataStk, i);
        dataStk += 4;
    }

    private int pop() {
        dataStk -= 4;
        return data.getInt(dataStk);
    }

    private void pushR(int i) {
        data.putInt(retStk, i);
        retStk += 4;
    }

    private int popR() {
        retStk -= 4;
        return data.getInt(retStk);
    }

    public boolean cycle() {
        if (received.size() > maxBuffer) {
            crashed = true;
        }
        if (sending.size() > maxBuffer) {
            crashed = true;
            sending.clear();
        }
        if (crashed) {
            received.clear();
            return true;
        }
        try {
            int off = data.position();
            execs[off]++;
            /*if (off == 554) { // Program-specific! Remove this.
             for (int i=0; i<execs.length; i++) {
             if (execs[i] != 0) {
             System.out.println("Exec " + i + " = " + execs[i]);
             }
             }
             crashed = true;
             return cycle();
             }*/
            int instr = data.get();
            if (DEBUG) {
                System.out.print("{" + (dataStk - 15360) / 4 + "," + (retStk - 15872) / 4 + "} ");
                if (dataStk - 15360 < 0) {
                    crashed = true;
                    return true;
                }
            }
            //System.out.println("Run: " + (data.position() - 1) + ": " + Integer.toHexString(instr));
            int rest = (instr >> 5) & 0x7;
            switch (instr & 0x1f) {
                case 0x00:
                    push(rest);
                    if (DEBUG) {
                        System.out.println(off + " push " + rest);
                    }
                    break;
                case 0x01:
                    if (DEBUG) {
                        System.out.println(off + " pop");
                    }
                    pop();
                    break;
                case 0x02:
                    if (DEBUG) {
                        System.out.println(off + " jumpi _");
                    }
                    data.position(pop());
                    break;
                case 0x03:
                    short s = data.getShort();
                    if (DEBUG) {
                        System.out.println(off + " jumpc " + s);
                    }
                    data.position(s);
                    break;
                case 0x04:
                    s = data.get();
                    if (rest == 1) {
                        s = (short) (-s - 2);
                    }
                    if (DEBUG) {
                        System.out.println(off + " jumpr " + s + " " + rest);
                    }
                    data.position(s + data.position());
                    break;
                case 0x05:
                    if (rest == 1) {
                        if (DEBUG) {
                            System.out.println(off + " rhere");
                        }
                        push(retStk);
                    } else if (rest == 1) {
                        if (DEBUG) {
                            System.out.println(off + " shere");
                        }
                        push(dataStk);
                    } else {
                        if (DEBUG) {
                            System.out.println(off + " here");
                        }
                        push(data.position());
                    }
                    break;
                case 0x06:
                    int top = pop();
                    if (DEBUG) {
                        System.out.println(off + " d2r " + top);
                    }
                    pushR(top);
                    break;
                case 0x07:
                    top = popR();
                    if (DEBUG) {
                        System.out.println(off + " r2d " + top);
                    }
                    push(top);
                    break;
                case 0x08:
                    top = pop();
                    if (DEBUG) {
                        System.out.println(off + " calli to " + top);
                    }
                    pushR(data.position());
                    data.position(top);
                    break;
                case 0x09:
                    s = data.getShort();
                    pushR(data.position());
                    data.position(s);
                    if (DEBUG) {
                        System.out.println(off + " callc to " + data.position());
                    }
                    break;
                case 0x0A:
                    byte r = data.get();
                    pushR(data.position());
                    data.position(r + data.position());
                    if (DEBUG) {
                        System.out.println(off + " callr " + r);
                    }
                    break;
                case 0x0B:
                    data.position(popR());
                    if (DEBUG) {
                        System.out.println(off + " ret to " + data.position());
                    }
                    break;
                case 0x0C:
                    top = pop();
                    r = data.get(top);
                    if (DEBUG) {
                        System.out.println(off + " getb [" + top + "] = " + (r & 0xff));
                    }
                    push(r & 0xff);
                    break;
                case 0x0D:
                    top = pop();
                    s = data.getShort(top);
                    if (DEBUG) {
                        System.out.println(off + " gets [" + top + "] = " + (s & 0xffff));
                    }
                    push(s & 0xffff);
                    break;
                case 0x0E:
                    top = pop();
                    int val = data.getInt(top);
                    if (DEBUG) {
                        System.out.println(off + " geti [" + top + "] = " + val);
                    }
                    push(val);
                    break;
                case 0x0F:
                    int base = pop();
                    r = (byte) pop();
                    if (DEBUG) {
                        System.out.println(off + " putb [" + base + "] = " + (r & 0xff));
                    }
                    data.put(base, r);
                    break;
                case 0x10:
                    base = pop();
                    s = (short) pop();
                    if (DEBUG) {
                        System.out.println(off + " puts [" + base + "] = " + (s & 0xffff));
                    }
                    data.putShort(base, s);
                    break;
                case 0x11:
                    base = pop();
                    top = pop();
                    if (DEBUG) {
                        System.out.println(off + " puti [" + base + "] = " + top);
                    }
                    data.putInt(base, top);
                    break;
                case 0x12:
                    int vi = pop();
                    int vj = pop();
                    if (DEBUG) {
                        System.out.println(off + " swap " + vj + " " + vi + ". -> " + vi + " " + vj + ".");
                    }
                    push(vi);
                    push(vj);
                    break;
                case 0x13:
                    vi = pop();
                    if (DEBUG) {
                        System.out.println(off + " dup " + vi);
                    }
                    push(vi);
                    push(vi);
                    break;
                case 0x14:
                    int per = pop();
                    top = pop();
                    if (DEBUG) {
                        System.out.println(off + " ctrlp " + per + " <- " + top);
                    }
                    control(top, per);
                    break;
                case 0x15:
                    if (DEBUG) {
                        System.out.println(off + " pause");
                    }
                    return true;
                case 0x16:
                    r = data.get();
                    if (DEBUG) {
                        System.out.println(off + " pushb " + (r & 0xff));
                    }
                    push(r & 0xff);
                    break;
                case 0x17:
                    s = data.getShort();
                    if (DEBUG) {
                        System.out.println(off + " pushs " + (s & 0xffff));
                    }
                    push(s & 0xffff);
                    break;
                case 0x18:
                    top = data.getInt();
                    if (DEBUG) {
                        System.out.println(off + " pushi " + top);
                    }
                    push(top);
                    break;
                case 0x19:
                    if (DEBUG) {
                        System.out.println(off + " jumpl " + rest);
                    }
                    data.position(rest + data.position());
                    break;
                case 0x1A: {
                    int b = pop();
                    int a = pop();
                    boolean o = false;
                    switch (rest) {
                        case 0: // eq
                            o = a == b;
                            if (DEBUG) {
                                System.out.println(off + " cmp-eq " + a + " " + b + " -> " + o);
                            }
                            break;
                        case 1: // ne
                            o = a != b;
                            if (DEBUG) {
                                System.out.println(off + " cmp-ne " + a + " " + b + " -> " + o);
                            }
                            break;
                        case 2: // lt
                            o = a < b;
                            if (DEBUG) {
                                System.out.println(off + " cmp-lt " + a + " " + b + " -> " + o);
                            }
                            break;
                        case 3: // gt
                            o = a > b;
                            if (DEBUG) {
                                System.out.println(off + " cmp-gt " + a + " " + b + " -> " + o);
                            }
                            break;
                        case 4: // le
                            o = a <= b;
                            if (DEBUG) {
                                System.out.println(off + " cmp-le " + a + " " + b + " -> " + o);
                            }
                            break;
                        case 5: // ge
                            o = a >= b;
                            if (DEBUG) {
                                System.out.println(off + " cmp-ge " + a + " " + b + " -> " + o);
                            }
                            break;
                        default:
                            System.err.println("Bad comparison: " + rest);
                            crashed = true;
                            break;
                    }
                    push(o ? 1 : 0);
                    break;
                }
                case 0x1B:
                    top = pop();
                    if (DEBUG) {
                        System.out.println(off + " jumplif " + top + " " + rest);
                    }
                    if (top != 0) {
                        data.position(rest + data.position());
                    }
                    break;
                case 0x1C:
                    if (rest == 5) { // Negate
                        int a = pop();
                        if (DEBUG) {
                            System.out.println(off + " negate " + a);
                        }
                        push(-a);
                    } else {
                        int b = pop();
                        int a = pop();
                        switch (rest) {
                            case 0:
                                if (DEBUG) {
                                    System.out.println(off + " add " + a + " " + b);
                                }
                                push(a + b);
                                break;
                            case 1:
                                if (DEBUG) {
                                    System.out.println(off + " sub " + a + " " + b);
                                }
                                push(a - b);
                                break;
                            case 2:
                                if (DEBUG) {
                                    System.out.println(off + " mul " + a + " " + b);
                                }
                                push(a * b);
                                break;
                            case 3:
                                if (DEBUG) {
                                    System.out.println(off + " div " + a + " " + b);
                                }
                                push(a / b); // TODO: Handle when b is zero
                                break;
                            case 4:
                                if (DEBUG) {
                                    System.out.println(off + " mod " + a + " " + b);
                                }
                                push(a % b);
                                break;
                            default:
                                System.err.println("Bad math: " + rest);
                                crashed = true;
                                break;
                        }
                    }
                    break;
                case 0x1D:
                    int b = pop();
                    int a = pop();
                    switch (rest) {
                        case 0:
                            if (DEBUG) {
                                System.out.println(off + " shl " + a + " " + b);
                            }
                            push(a << b);
                            break;
                        case 1:
                            if (DEBUG) {
                                System.out.println(off + " shr " + a + " " + b);
                            }
                            push(a >> b);
                            break;
                        case 2:
                            if (DEBUG) {
                                System.out.println(off + " ushr " + a + " " + b);
                            }
                            push(a >>> b);
                            break;
                        case 3:
                            if (DEBUG) {
                                System.out.println(off + " and " + a + " " + b);
                            }
                            push(a & b);
                            break;
                        case 4:
                            if (DEBUG) {
                                System.out.println(off + " or " + a + " " + b);
                            }
                            push(a | b);
                            break;
                        case 5:
                            if (DEBUG) {
                                System.out.println(off + " xor " + a + " " + b);
                            }
                            push(a ^ b);
                            break;
                        default:
                            System.err.println("Bad log: " + rest);
                            crashed = true;
                            break;
                    }
                    break;
                case 0x1E: {
                    if (rest == 0) {
                        int bytecount = pop();
                        {
                            int to = pop();
                            int from = pop();
                            if (bytecount <= 0) {
                                if (DEBUG) {
                                    System.out.println(off + " bytec END");
                                }
                                break;
                            }
                            if (DEBUG) {
                                System.out.println(off + " bytec " + to + "<-" + from + "#" + bytecount);
                            }
                            data.put(to, data.get(from));
                            push(from + 1);
                            push(to + 1);
                            bytecount--;
                        }
                        {
                            int to = pop();
                            int from = pop();
                            if (bytecount <= 0) {
                                if (DEBUG) {
                                    System.out.println(off + " bytec.2 END");
                                }
                                break;
                            }
                            if (DEBUG) {
                                System.out.println(off + " bytec.2 " + to + "<-" + from + "#" + bytecount);
                            }
                            data.put(to, data.get(from));
                            push(from + 1);
                            push(to + 1);
                            push(bytecount - 1);
                            data.position(data.position() - 1);
                        }
                        break;
                    } else if (rest == 1) {
                        int bytecount = pop();
                        int to = pop();
                        int fill = pop();
                        if (bytecount <= 0) {
                            if (DEBUG) {
                                System.out.println(off + " bytef END");
                            }
                            break;
                        }
                        if (DEBUG) {
                            System.out.println(off + " bytef " + to + "<-" + fill + "#" + bytecount);
                        }
                        data.put(to, (byte) fill);
                        push(fill);
                        push(to + 1);
                        push(bytecount - 1);
                        data.position(data.position() - 1);
                        break;
                    } else {
                        if (DEBUG) {
                            System.out.println(off + " unhandled 0x1E+?");
                        }
                        break;
                    }
                }
                case 0x1F:
                    if (rest == 0) {
                        top = pop();
                        if (DEBUG) {
                            System.out.println(off + " sxt-byte " + top);
                        }
                        push((byte) top);
                    } else if (rest == 1) {
                        top = pop();
                        if (DEBUG) {
                            System.out.println(off + " sxt-short " + top);
                        }
                        push((short) top);
                    } else {
                        System.err.println("Bad sign extend: " + rest);
                        crashed = true;
                    }
                    break;
            }
        } catch (IndexOutOfBoundsException | BufferUnderflowException | BufferOverflowException e) {
            // Crash the system
            // Later: interrupts!
            e.printStackTrace();
            crashed = true;
        }
        return crashed;
    }

    private void control(int ctrlm, int per) {
        switch (per) {
            case 0: // Console
                if (ctrlm == 0) {
                    for (int i = 0; i < 16; i++) {
                        try {
                            screen[i] = new String(data.array(), i * 64 + 16384, 64, "US-ASCII").replace('\0', ' ');
                        } catch (UnsupportedEncodingException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
                break;
            case 1: // Disk
                if (ctrlm >= 0 && ctrlm < 32) {
                    diskLoad(ctrlm);
                } else if (ctrlm >= 32 && ctrlm < 64) {
                    diskSave(ctrlm - 32);
                }
                break;
            case 2: // Network
                if (ctrlm == 0) {
                    netNext();
                } else if (ctrlm == 1) {
                    netSend();
                }
                break;
            case 3: // Keyboard
                if (ctrlm == 0) {
                    keyShift();
                } else if (ctrlm == 1) {
                    keyClear();
                }
                break;
        }
    }
}

/* CURRENT DOCUMENTATION
 Two stacks: return and data - can't exceed 128 ints each.
 Data space as well - can't exceed 15 KB.
 Then peripherals.

 Console screen:
 64 columns, 16 rows

 Memory space:
 0-15359: Data space
 15360-15871: Data stack
 15872-16383: Return stack
 16384-17407: Port 0: Console screen
 17408-18431: Port 1: Disk drive
 18432-19455: Port 2: Network card (@18432 = short: prewritten network address, modification ignored)
 19456-20479: Port 3: Keyboard (@19456 = short: length, rest is data)

 Network mapping:
 18432-18433: Hardware address, or 0 if no network connection. [Note: currently never 0.]
 18434-18435: Received from for this message, or 0 if no current message.
 18436-18943: Received message
 18944-18945: Unused short
 18946-18947: Target address, or 0 to broadcast
 18948-19455: Sending message

 Control messages:
 0: Console screen
 - 0: Update screen from console memory!
 1: Disk
 - 0-31: Load block 0-31 from disk into memory.
 - 32-63: Save block 0-31 from memory onto disk.
 2: Network
 - 0: Fetch next message if possible, or set to 0 message if no new message.
 - 1: Send message
 3: Keyboard
 - 0: Shift first character
 - 1: Clear

 IP register.

 Big-endian. (MSB first)

 <separate bits of code/bit length>
 [rest of byte]

 Instruction set:
 00	pushn [int]
 01	pop
 02	jumpi - go to popped from data
 03	jumpc <ptr/16>
 04<0>	jumpr <rel/8> - jump relative to next instruction
 04<1>	jumprn <rel/8> - same as jumpr except jump backwards from start of instruction
 05	here - get IP (the one after the instruction has executed)
 06	d2r - data to return
 07	r2d - return to data
 08	calli - push next IP to return, go to popped from data
 09	callc <ptr/16>
 0A	callr <rel/8>
 0B	ret - return to popped from return stack
 0C	getb - get byte from specified location
 0D	gets - get short from specified location
 0E	geti - get int from specified location
 0F	putb - put byte to specified location (address on top of stack)
 10	puts - put short
 11	puti - put int
 12	swap - swap the top two items
 13	dup - duplicate the top item
 14	ctrlp - send control message (under top) to peripheral (top)
 15	pause - wait until next cycle
 16	pushb <byte>
 17	pushs <short>
 18	pushi <int>
 19	jumpl [rel]
 1A	cmp [type] - perform comparison on two values. push 1 if true, 0 if false.
 1B	jumplif [rel] - jumpl if popped value is not zero
 1C	math [type] <a> <b>? - run math: add, sub, mul, div, rem, neg
 1D	log [type] <a> <b> - run math: shl, shr, ushr, and, or, xor
 1E<0>	bytec - (from, to, bytecount). If bytecount == 0, continue to next instruction. Otherwise copy a byte, increment positions, decrement bytecount, and stay at this instruction.
 1E<1>	bytef - (byte, to, bytecount). If bytecount == 0, continue to next instruction. Otherwise set [to] = byte, to++, bytecount--, continue at this instruction.
 1F	sxt [from] - Sign extend from byte (0), short (1).
 */
