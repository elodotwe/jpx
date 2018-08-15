/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.jpx;

import static java.lang.String.format;

import us.fatehi.pointlocation6709.Angle;
import us.fatehi.pointlocation6709.format.FormatterException;
import us.fatehi.pointlocation6709.format.PointLocationFormatType;
import us.fatehi.pointlocation6709.format.PointLocationFormatter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LocationFormatterTest {

	private static final String BASE_DIR = "jpx/src/test/resources/io/jenetics/jpx/iso6709";


	@Test(dataProvider = "humanLongLatitudes")
	public void humanLongLatitudes(final String[] row) {
		final double degrees = Double.parseDouble(row[0]);
		final Latitude latitude = Latitude.ofDegrees(degrees);

		final String format = row[1];
		Assert.assertEquals(
			LocationFormatter.ISO_HUMAN_LONG.format(latitude),
			format
		);
	}

	@DataProvider(name = "humanLongLatitudes")
	public Iterator<Object[]> humanLongLatitudes() throws IOException {
		final File file  = new File("" +
			"src/test/resources/io/jenetics/jpx/iso6709",
			"latitudes[ISO_HUMAN_LONG].csv"
		);
		final List<String> lines = Files.readAllLines(file.toPath());

		return lines.stream()
			.map(line -> line.split("\t"))
			.map(row -> (Object[])row)
			.collect(Collectors.toList())
			.iterator();
	}



	@Test
	public void latitude() throws FormatterException {
		final Random random = new Random();

		final Latitude latitude = LocationRandom.nextLatitude(random);
		final us.fatehi.pointlocation6709.Latitude lat = toLat(latitude);

		System.out.println(latitude);

		for (PointLocationFormatType type : PointLocationFormatType.values()) {
			try {
				System.out.println(
					type.toString() + ": " +
						PointLocationFormatter.formatLatitude(lat, type)
				);
			} catch (Exception e) {
				System.out.println("Not supported: " + type);
			}
		}
	}

	static us.fatehi.pointlocation6709.Latitude toLat(final Latitude latitude) {
		final Angle angle = us.fatehi.pointlocation6709.Latitude
			.fromDegrees(latitude.toDegrees());

		return new us.fatehi.pointlocation6709.Latitude(angle);
	}

	public static void main(final String[] args) throws IOException, FormatterException {
		final Random random = new Random(191929);
		final List<Latitude> latitudes = Stream.generate(() -> LocationRandom.nextLatitude(random))
			.limit(100)
			.collect(Collectors.toList());

		write(latitudes);
	}

	private static void write(final List<Latitude> latitudes)
		throws IOException, FormatterException
	{
		final File baseDir = new File(BASE_DIR);
		if (!baseDir.isDirectory() && !baseDir.mkdirs()) {
			throw new IOException("Error while creating directory " + baseDir);
		}

		final StringBuilder out = new StringBuilder();
		for (Latitude latitude : latitudes) {
			final us.fatehi.pointlocation6709.Latitude lat = toLat(latitude);
			final String string = PointLocationFormatter.formatLatitude(
				lat, PointLocationFormatType.HUMAN_LONG
			);

			out.append(format("%f\t%s\n", latitude.toDegrees(), string));
		}

		final File file = new File(baseDir, "latitudes[ISO_HUMAN_LONG].csv");
		Files.write(file.toPath(), out.toString().getBytes());
	}

}
