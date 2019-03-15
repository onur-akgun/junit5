/*
 * Copyright 2015-2019 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.platform.console.options;

import static org.apiguardian.api.API.Status.INTERNAL;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ServiceLoader;

import org.apiguardian.api.API;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.engine.ConfigurationParameter;
import org.junit.platform.engine.TestEngine;

import picocli.CommandLine;
import picocli.CommandLine.ParseResult;

/**
 * @since 1.0
 */
@API(status = INTERNAL, since = "1.0")
public class PicocliCommandLineOptionsParser implements CommandLineOptionsParser {

	@Override
	public CommandLineOptions parse(String... arguments) {
		AvailableOptions availableOptions = getAvailableOptions();
		CommandLine parser = availableOptions.getParser();
		try {
			ParseResult detectedOptions = parser.parseArgs(arguments);
			return availableOptions.toCommandLineOptions(detectedOptions);
		}
		catch (Exception e) {
			throw new JUnitException("Error parsing command-line arguments: " + e.getMessage(), e);
		}
	}

	@Override
	public void printHelp(Writer writer) {
		try {
			writer.write(getAvailableOptions().getParser().getUsageMessage());

			PrintWriter out = new PrintWriter(writer);
			out.println("##");
			out.println("## JUnit Platform Properties");
			out.println("##");
			out.println("");
			out.println("# n.a.");
			out.println("");
			for (TestEngine engine : ServiceLoader.load(TestEngine.class)) {
				for (ConfigurationParameter parameter : engine.getAvailableConfigurationParameters()) {
					out.println("# Description: " + parameter.getDescription());
					out.println("#" + parameter.getKey() + " = " + parameter.getDefaultValue());
				}
			}
		}
		catch (IOException e) {
			throw new JUnitException("Error printing help", e);
		}
	}

	private AvailableOptions getAvailableOptions() {
		return new AvailableOptions();
	}
}
