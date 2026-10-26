import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.File;

/** Le teclas individuais em terminais POSIX e restaura sua configuracao ao sair. */
public class TerminalInput implements AutoCloseable {
    public static final int UP = 1001, DOWN = 1002, RIGHT = 1003, LEFT = 1004;
    private final String originalSettings;
    private final Thread restoreHook;
    private boolean closed;

    public TerminalInput() throws IOException, InterruptedException {
        originalSettings = stty("-g").trim();
        restoreHook = new Thread(this::close);
        Runtime.getRuntime().addShutdownHook(restoreHook);
        try {
            stty("-icanon", "-echo", "min", "1", "time", "0");
        } catch (IOException | InterruptedException exception) {
            close();
            throw exception;
        }
    }

    private static String stty(String... arguments) throws IOException, InterruptedException {
        String[] command = new String[arguments.length + 1];
        command[0] = "stty";
        System.arraycopy(arguments, 0, command, 1, arguments.length);
        Process process = new ProcessBuilder(command)
            .redirectInput(new File("/dev/tty")).redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        if (process.waitFor() != 0) throw new IOException("Nao foi possivel configurar o terminal.");
        return output;
    }

    public int readKey() throws IOException {
        int key = System.in.read();
        if (key != 27) return key;
        // As setas chegam como ESC [ A/B/C/D (ou ESC O A/B/C/D).
        // Ignora ESC isolado sem bloquear a entrada indefinidamente.
        if (!awaitByte()) return 27;
        int prefix = System.in.read();
        if (prefix != '[' && prefix != 'O') return 0;
        if (!awaitByte()) return 0;
        int code = System.in.read();
        while (code >= 0x20 && code <= 0x3f) {
            if (!awaitByte()) return 0;
            code = System.in.read();
        }
        switch (code) {
            case 'A': return UP;
            case 'B': return DOWN;
            case 'C': return RIGHT;
            case 'D': return LEFT;
            default: return 0;
        }
    }

    private boolean awaitByte() throws IOException {
        long deadline = System.nanoTime() + 100_000_000L;
        while (System.in.available() == 0 && System.nanoTime() < deadline) {
            try { Thread.sleep(1); }
            catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return System.in.available() > 0;
    }

    @Override
    public synchronized void close() {
        if (closed) return;
        closed = true;
        try { stty(originalSettings); }
        catch (IOException exception) { System.err.println("Nao foi possivel restaurar o terminal."); }
        catch (InterruptedException exception) { Thread.currentThread().interrupt(); }
        if (Thread.currentThread() != restoreHook) Runtime.getRuntime().removeShutdownHook(restoreHook);
    }
}
