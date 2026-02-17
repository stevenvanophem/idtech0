package be.envano.games.wolf3d;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * SDL3/FFM platform layer used by milestone replacements.
 */
public final class ID_SDL {

    // SDL_INIT_VIDEO
    private static final int SDL_INIT_VIDEO = 0x00000020;
    // SDL event type constant
    private static final int SDL_EVENT_QUIT = 0x100;
    private static boolean sdlVideoInitialized;
    private static MemorySegment sdlWindow = MemorySegment.NULL;
    private static SymbolLookup sdlLookup;
    private static Linker sdlLinker;
    private static MethodHandle SDL_Init;
    private static MethodHandle SDL_CreateWindow;
    private static MethodHandle SDL_PollEvent;
    private static MethodHandle SDL_Delay;
    private static MethodHandle SDL_GetError;

    private ID_SDL() {
    }

    static void SDL_SetVideoMode_Windowed() {
        if (sdlVideoInitialized && sdlWindow.address() != 0L) {
            return;
        }

        ensureSdlApiLoaded();
        try {
            boolean initOk = (boolean) SDL_Init.invokeExact((long) SDL_INIT_VIDEO);
            if (!initOk) {
                throw new IllegalStateException("SDL_Init failed: " + getSdlError());
            }

            try (Arena arena = Arena.ofConfined()) {
                MemorySegment title = arena.allocateFrom("Wolf3D Java Port");
                MemorySegment window = (MemorySegment) SDL_CreateWindow.invokeExact(title, 640, 400, 0L);
                if (window.address() == 0L) {
                    throw new IllegalStateException("SDL_CreateWindow failed: " + getSdlError());
                }
                sdlWindow = window;
            }

            sdlVideoInitialized = true;
        } catch (Throwable t) {
            throw new RuntimeException("Failed to initialize SDL3 video/window via FFM", t);
        }
    }

    static boolean SDL_RunEventLoopTick() {
        if (!sdlVideoInitialized || sdlWindow.address() == 0L) {
            return false;
        }

        ensureSdlApiLoaded();
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment event = arena.allocate(128);
            boolean hasEvent = (boolean) SDL_PollEvent.invokeExact(event);
            if (hasEvent) {
                int type = event.get(ValueLayout.JAVA_INT, 0);
                if (type == SDL_EVENT_QUIT) {
                    return true;
                }
            } else {
                SDL_Delay.invokeExact(16);
            }
            return false;
        } catch (Throwable t) {
            throw new RuntimeException("Failed while running SDL event loop via FFM", t);
        }
    }

    private static Path resolveSdl3DllPath() {
        Path p1 = Path.of("native", "windows-x64", "SDL3.dll");
        if (Files.exists(p1)) {
            return p1;
        }
        Path p2 = Path.of("javaport", "native", "windows-x64", "SDL3.dll");
        if (Files.exists(p2)) {
            return p2;
        }
        throw new IllegalStateException("SDL3.dll not found in expected native/windows-x64 locations");
    }

    private static void ensureSdlApiLoaded() {
        if (sdlLookup != null) {
            return;
        }
        Path dll = resolveSdl3DllPath();
        sdlLookup = SymbolLookup.libraryLookup(dll, Arena.global());
        sdlLinker = Linker.nativeLinker();
        try {
            SDL_Init = sdlLinker.downcallHandle(
                    sdlLookup.find("SDL_Init").orElseThrow(),
                    FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_LONG)
            );
            SDL_CreateWindow = sdlLinker.downcallHandle(
                    sdlLookup.find("SDL_CreateWindow").orElseThrow(),
                    FunctionDescriptor.of(
                            ValueLayout.ADDRESS,
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_INT,
                            ValueLayout.JAVA_LONG
                    )
            );
            SDL_PollEvent = sdlLinker.downcallHandle(
                    sdlLookup.find("SDL_PollEvent").orElseThrow(),
                    FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS)
            );
            SDL_Delay = sdlLinker.downcallHandle(
                    sdlLookup.find("SDL_Delay").orElseThrow(),
                    FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT)
            );
            SDL_GetError = sdlLinker.downcallHandle(
                    sdlLookup.find("SDL_GetError").orElseThrow(),
                    FunctionDescriptor.of(ValueLayout.ADDRESS)
            );
        } catch (Throwable t) {
            throw new RuntimeException("Failed to bind SDL3 symbols via FFM", t);
        }
    }

    private static String getSdlError() {
        try {
            MemorySegment ptr = (MemorySegment) SDL_GetError.invokeExact();
            if (ptr == null || ptr.address() == 0L) {
                return "<no SDL error>";
            }
            return ptr.reinterpret(Long.MAX_VALUE).getString(0);
        } catch (Throwable t) {
            return "<SDL_GetError unavailable: " + t.getClass().getSimpleName() + ">";
        }
    }
}
