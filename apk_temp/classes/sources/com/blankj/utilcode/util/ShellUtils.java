package com.blankj.utilcode.util;

import com.blankj.utilcode.util.Utils;
import java.util.List;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class ShellUtils {
    private static final String LINE_SEP = System.getProperty("line.separator");

    private ShellUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Utils.Task<CommandResult> execCmdAsync(String command, boolean isRooted, Utils.Consumer<CommandResult> consumer) {
        return execCmdAsync(new String[]{command}, isRooted, true, consumer);
    }

    public static Utils.Task<CommandResult> execCmdAsync(List<String> commands, boolean isRooted, Utils.Consumer<CommandResult> consumer) {
        return execCmdAsync(commands == null ? null : (String[]) commands.toArray(new String[0]), isRooted, true, consumer);
    }

    public static Utils.Task<CommandResult> execCmdAsync(String[] commands, boolean isRooted, Utils.Consumer<CommandResult> consumer) {
        return execCmdAsync(commands, isRooted, true, consumer);
    }

    public static Utils.Task<CommandResult> execCmdAsync(String command, boolean isRooted, boolean isNeedResultMsg, Utils.Consumer<CommandResult> consumer) {
        return execCmdAsync(new String[]{command}, isRooted, isNeedResultMsg, consumer);
    }

    public static Utils.Task<CommandResult> execCmdAsync(List<String> commands, boolean isRooted, boolean isNeedResultMsg, Utils.Consumer<CommandResult> consumer) {
        return execCmdAsync(commands == null ? null : (String[]) commands.toArray(new String[0]), isRooted, isNeedResultMsg, consumer);
    }

    public static Utils.Task<CommandResult> execCmdAsync(final String[] commands, final boolean isRooted, final boolean isNeedResultMsg, Utils.Consumer<CommandResult> consumer) {
        return UtilsBridge.doAsync(new Utils.Task<CommandResult>(consumer) { // from class: com.blankj.utilcode.util.ShellUtils.1
            @Override // com.blankj.utilcode.util.ThreadUtils.Task
            public CommandResult doInBackground() {
                return ShellUtils.execCmd(commands, isRooted, isNeedResultMsg);
            }
        });
    }

    public static CommandResult execCmd(String command, boolean isRooted) {
        return execCmd(new String[]{command}, isRooted, true);
    }

    public static CommandResult execCmd(String command, List<String> envp, boolean isRooted) {
        return execCmd(new String[]{command}, envp == null ? null : (String[]) envp.toArray(new String[0]), isRooted, true);
    }

    public static CommandResult execCmd(List<String> commands, boolean isRooted) {
        return execCmd(commands == null ? null : (String[]) commands.toArray(new String[0]), isRooted, true);
    }

    public static CommandResult execCmd(List<String> commands, List<String> envp, boolean isRooted) {
        return execCmd(commands == null ? null : (String[]) commands.toArray(new String[0]), envp != null ? (String[]) envp.toArray(new String[0]) : null, isRooted, true);
    }

    public static CommandResult execCmd(String[] commands, boolean isRooted) {
        return execCmd(commands, isRooted, true);
    }

    public static CommandResult execCmd(String command, boolean isRooted, boolean isNeedResultMsg) {
        return execCmd(new String[]{command}, isRooted, isNeedResultMsg);
    }

    public static CommandResult execCmd(String command, List<String> envp, boolean isRooted, boolean isNeedResultMsg) {
        return execCmd(new String[]{command}, envp == null ? null : (String[]) envp.toArray(new String[0]), isRooted, isNeedResultMsg);
    }

    public static CommandResult execCmd(String command, String[] envp, boolean isRooted, boolean isNeedResultMsg) {
        return execCmd(new String[]{command}, envp, isRooted, isNeedResultMsg);
    }

    public static CommandResult execCmd(List<String> commands, boolean isRooted, boolean isNeedResultMsg) {
        return execCmd(commands == null ? null : (String[]) commands.toArray(new String[0]), isRooted, isNeedResultMsg);
    }

    public static CommandResult execCmd(String[] commands, boolean isRooted, boolean isNeedResultMsg) {
        return execCmd(commands, (String[]) null, isRooted, isNeedResultMsg);
    }

    /* JADX WARN: Can't wrap try/catch for region: R(13:7|(9:138|8|(1:10)(1:11)|12|117|13|(3:15|(2:17|142)(2:18|141)|19)|140|20)|(4:22|(2:24|(2:25|(1:27)(1:143)))(0)|28|(11:30|(2:31|(1:33)(0))|36|(2:129|41)|(2:133|47)|(1:53)|82|(1:84)(1:85)|(1:88)|89|90)(0))(0)|121|36|(0)|(0)|(0)|82|(0)(0)|(0)|89|90) */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00d6, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00d7, code lost:
    
        r0.printStackTrace();
     */
    /* JADX WARN: Removed duplicated region for block: B:112:0x017b  */
    /* JADX WARN: Removed duplicated region for block: B:119:0x0151 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:123:0x015f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:125:0x016d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:129:0x00de A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:133:0x00ec A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:144:? A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:53:0x00fa A[PHI: r2 r4 r7 r8
      0x00fa: PHI (r2v3 'result' int) = (r2v1 'result' int), (r2v5 'result' int) binds: [B:80:0x0135, B:52:0x00f8] A[DONT_GENERATE, DONT_INLINE]
      0x00fa: PHI (r4v5 'process' java.lang.Process) = (r4v4 'process' java.lang.Process), (r4v7 'process' java.lang.Process) binds: [B:80:0x0135, B:52:0x00f8] A[DONT_GENERATE, DONT_INLINE]
      0x00fa: PHI (r7v3 'successMsg' java.lang.StringBuilder) = (r7v1 'successMsg' java.lang.StringBuilder), (r7v5 'successMsg' java.lang.StringBuilder) binds: [B:80:0x0135, B:52:0x00f8] A[DONT_GENERATE, DONT_INLINE]
      0x00fa: PHI (r8v3 'errorMsg' java.lang.StringBuilder) = (r8v1 'errorMsg' java.lang.StringBuilder), (r8v5 'errorMsg' java.lang.StringBuilder) binds: [B:80:0x0135, B:52:0x00f8] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:84:0x013c  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x013e  */
    /* JADX WARN: Removed duplicated region for block: B:88:0x0145  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static com.blankj.utilcode.util.ShellUtils.CommandResult execCmd(java.lang.String[] r15, java.lang.String[] r16, boolean r17, boolean r18) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 391
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.ShellUtils.execCmd(java.lang.String[], java.lang.String[], boolean, boolean):com.blankj.utilcode.util.ShellUtils$CommandResult");
    }

    public static class CommandResult {
        public String errorMsg;
        public int result;
        public String successMsg;

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }

        public String toString() {
            return "result: " + this.result + "\nsuccessMsg: " + this.successMsg + "\nerrorMsg: " + this.errorMsg;
        }
    }
}
