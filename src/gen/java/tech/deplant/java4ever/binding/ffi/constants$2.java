// Generated by jextract

package tech.deplant.java4ever.binding.ffi;

import java.lang.invoke.MethodHandle;
import java.lang.foreign.*;

class constants$2 {

    static final FunctionDescriptor tc_request_ptr$FUNC = FunctionDescriptor.ofVoid(
        Constants$root.C_LONG$LAYOUT,
        MemoryLayout.structLayout(
            Constants$root.C_POINTER$LAYOUT.withName("content"),
            Constants$root.C_LONG$LAYOUT.withName("len"),
            MemoryLayout.paddingLayout(32)
        ),
        MemoryLayout.structLayout(
            Constants$root.C_POINTER$LAYOUT.withName("content"),
            Constants$root.C_LONG$LAYOUT.withName("len"),
            MemoryLayout.paddingLayout(32)
        ),
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle tc_request_ptr$MH = RuntimeHelper.downcallHandle(
        "tc_request_ptr",
        constants$2.tc_request_ptr$FUNC
    );
    static final FunctionDescriptor tc_request_sync$FUNC = FunctionDescriptor.of(Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_LONG$LAYOUT,
        MemoryLayout.structLayout(
            Constants$root.C_POINTER$LAYOUT.withName("content"),
            Constants$root.C_LONG$LAYOUT.withName("len"),
            MemoryLayout.paddingLayout(32)
        ),
        MemoryLayout.structLayout(
            Constants$root.C_POINTER$LAYOUT.withName("content"),
            Constants$root.C_LONG$LAYOUT.withName("len"),
            MemoryLayout.paddingLayout(32)
        )
    );
    static final MethodHandle tc_request_sync$MH = RuntimeHelper.downcallHandle(
        "tc_request_sync",
        constants$2.tc_request_sync$FUNC
    );
    static final FunctionDescriptor tc_read_string$FUNC = FunctionDescriptor.of(MemoryLayout.structLayout(
        Constants$root.C_POINTER$LAYOUT.withName("content"),
        Constants$root.C_LONG$LAYOUT.withName("len"),
        MemoryLayout.paddingLayout(32)
    ),
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle tc_read_string$MH = RuntimeHelper.downcallHandle(
        "tc_read_string",
        constants$2.tc_read_string$FUNC
    );
    static final FunctionDescriptor tc_destroy_string$FUNC = FunctionDescriptor.ofVoid(
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle tc_destroy_string$MH = RuntimeHelper.downcallHandle(
        "tc_destroy_string",
        constants$2.tc_destroy_string$FUNC
    );
    static final MemoryAddress NULL$ADDR = MemoryAddress.ofLong(0L);
}

