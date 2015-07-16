/*
 * Copyright (c) 2015 Nikolay Zapolnov (zapolnov@gmail.com).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.zapolnov.buildsystem;

import com.bulenkov.darcula.DarculaLaf;
import com.zapolnov.buildsystem.build.Generator;
import com.zapolnov.buildsystem.build.ProjectBuilder;
import com.zapolnov.buildsystem.gui.FatalErrorDialog;
import com.zapolnov.buildsystem.gui.MainDialog;
import com.zapolnov.buildsystem.project.Project;
import com.zapolnov.buildsystem.project.ProjectReader;
import com.zapolnov.buildsystem.utility.FileUtils;
import com.zapolnov.buildsystem.utility.StringUtils;
import java.io.File;
import javax.swing.UIManager;

/** Main class of the application. */
public class Main
{
    /** Prints the command line usage information.  */
    public static void printUsage()
    {
                         // 12345678901234567890123456789012345678901234567890123456789012345678901234567890
        System.out.println("");
        System.out.println("Usage: java -jar buildsystem.jar [options]");
        System.out.println("Where options are:");
        System.out.println("  -h, --help               Display this help screen.");
        System.out.println("  -b, --batch              Run in batch mode (no GUI).");
        System.out.println("  -x, --stacktraces        Print stack traces for exceptions (batch mode).");
        System.out.println("  -p, --project <path>     Specify path to the project directory (batch mode).");
        System.out.println("  -g, --generator <name>   Specify generator to use (batch mode).");
        System.out.println("");
    }

    /**
     * Checks whether given command line argument enables batch (no GUI) mode.
     * @param arg Command line argument.
     * @return `true` if the given command line argument enables batch mode, otherwise returns `false`.
     */
    public static boolean isBatchModeArgument(String arg)
    {
        return ("--batch".equals(arg) || "-b".equals(arg));
    }

    /**
     * Retrieves value of the parameter of the command line argument.
     * @param args Array of command line arguments.
     * @param i Index of the parameter in the array.
     */
    public static String getCommandLineArgumentParameter(String[] args, int i)
    {
        if (i >= args.length) {
            System.err.println(String.format("Missing value after the command line argument \"%s\".", args[i - 1]));
            System.exit(1);
        }
        return args[i];
    }

    /**
     * Retrieves generator by it's name.
     * @param className Name of the generator class.
     */
    public static Generator getGenerator(String className) throws ClassNotFoundException
    {
        String[] formats = new String[]{ "%s", "com.zapolnov.buildsystem.build.%s" };

        for (String format : formats) {
            String fullClassName = String.format(format, className);
            try {
                Class<?> generatorClass = Class.forName(fullClassName);
                if (generatorClass != null) {
                    Object generator = generatorClass.newInstance();
                    if (generator instanceof Generator)
                        return (Generator)generator;
                }
            } catch (ClassNotFoundException ignored) {
            } catch (InstantiationException|IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        throw new ClassNotFoundException(className);
    }

    /**
     * Runs tool in the batch (console) mode.
     * @param args Command line arguments.
     */
    public static void runBatchMode(String[] args)
    {
        boolean printStackTraces = false;
        String generatorClassName = null;
        File projectDirectory = new File(".");

        try {
            for (int i = 0; i < args.length; i++) {
                if ("--help".equals(args[i]) || "-h".equals(args[i])) {
                    printUsage();
                    System.exit(1);
                } else if ("--stacktraces".equals(args[i]) || "-x".equals(args[i])) {
                    printStackTraces = true;
                } else if ("--project".equals(args[i]) || "-p".equals(args[i])) {
                    projectDirectory = new File(getCommandLineArgumentParameter(args, ++i));
                } else if ("--generator".equals(args[i]) || "-g".equals(args[i])) {
                    generatorClassName = getCommandLineArgumentParameter(args, ++i);
                } else if (!isBatchModeArgument(args[i])) {
                    System.err.println(String.format("ERROR: Invalid command line argument \"%s\".", args[i]));
                    System.exit(1);
                }
            }

            Project project = ProjectReader.read(FileUtils.getCanonicalFile(projectDirectory));
            ProjectBuilder projectBuilder = new ProjectBuilder(project);

            if (generatorClassName == null) {
                System.err.println("Generator was not specified on the command line.");
                System.exit(1);
            }
            projectBuilder.setGenerator(getGenerator(generatorClassName));

            projectBuilder.run();
        } catch (Throwable t) {
            if (printStackTraces)
                System.err.println(StringUtils.getDetailedExceptionMessage(t));
            else
                System.err.println(StringUtils.getShortExceptionMessage(t));
            System.exit(1);
        }
    }

    /**
     * Runs tool in the GUI mode.
     * @param args Command line arguments.
     */
    public static void runGuiMode(String[] args)
    {
        try {
            try {
                UIManager.setLookAndFeel(new DarculaLaf());
            } catch (Throwable ignored) {
            }

            for (String arg : args) {
                if (!isBatchModeArgument(arg))
                    throw new RuntimeException(String.format("Invalid command line argument \"%s\".", arg));
            }

            MainDialog.run();
        } catch (Throwable t) {
            System.err.println(StringUtils.getDetailedExceptionMessage(t));
            FatalErrorDialog.run(t);
        }
    }

    /**
     * Application entry point.
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        for (String arg : args) {
            if (isBatchModeArgument(arg)) {
                runBatchMode(args);
                return;
            }
        }

        runGuiMode(args);
    }
}
