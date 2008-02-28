/**
 * Copyright (C) 2007, Jens Lehmann
 *
 * This file is part of DL-Learner.
 * 
 * DL-Learner is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * DL-Learner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.dllearner.core.config;

/**
 * A config entry is a configuration option and a value for the option.
 * 
 * @author Jens Lehmann
 * 
 */
public class ConfigEntry<T> {

	private ConfigOption<T> option;
	private T value;

	public ConfigEntry(ConfigOption<T> option, T value) throws InvalidConfigOptionValueException {
		if (!option.isValidValue(value)) {
			throw new InvalidConfigOptionValueException(option, value);
		} else {
			this.option = option;
			this.value = value;
		}
	}

	public ConfigOption<T> getOption() {
		return option;
	}

	public String getOptionName() {
		return option.getName();
	}

	public T getValue() {
		return value;
	}

	/**
	 * Get a string to save into a configuration file.
	 * 
	 * @return a formatted string
	 */
	public String toConfString(String componentName) {
		if (option.getName() == "positiveExamples") {
			return option.getValueFormatting(value, 1);
		} else if (option.getName() == "negativeExamples") {
			return option.getValueFormatting(value, 2);
		} else
			return componentName.toString() + "." + option.getName() + " = "
					+ option.getValueFormatting(value, 0);
	}
}
