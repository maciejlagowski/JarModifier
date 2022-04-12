package io.github.maciejlagowski.jarmodifier.service.decompiler;

import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

class Decompiler extends ConsoleDecompiler {

    protected Decompiler(File destination, Map<String, Object> options, IFernflowerLogger logger) {
        super(destination, options, logger);
    }

    public static void decompile(String pathToJar) {
        Map<String, Object> mapOptions = new HashMap<>();
        mapOptions.put("dgs", "1");
        File file = new File(pathToJar);

        File destination = new File("decompiled");
        PrintStreamLogger logger = new PrintStreamLogger(System.out);
        ConsoleDecompiler decompiler = new Decompiler(destination, mapOptions, logger);

        Fernflower engine = new Fernflower(decompiler, decompiler, mapOptions, logger);

        engine.addSource(file);

        engine.decompileContext();
    }
}
