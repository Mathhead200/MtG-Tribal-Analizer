
/**
 * @param {String} creature_subtypes e.g., "Human", or "Elf Wizard"
 * @param {String} colors_identity e.g., "W" for mono-white, "GW" for Selesnya, or "" for Colorless
 * @returns {String}
 */
const gathererURL = (creature_subtypes, colors_identity) => {
	const url = "https://gatherer.wizards.com/Pages/Search/Default.aspx?action=advanced&type=+[%22Creature%22]";

	// add subtypes
	if (creature_subtypes != null) {
		url += "&subtype=";
		for (let subtype of creature_subtypes.split(/\s+/))
			url += `+[%22${subtype}%22]`;
	}

	// add color identity
	if (colors_identity != null) {
		url += "&color=";
		color_map = { "W": false, "U": false, "B": false, "R": false, "G": false };
		for (const c of colors_identity)
			color_map[c.toUpperCase()] = true;
		for (const c in color_map) {
			url += "+";
			if (!color_map[color])
				url += "!";
			url += `[%22${color}%22]`;
		}
	}
};

/**
 * 
 * @param {Array} tribes
 * @return {*} JSON object containing the scraped data aout those tribes
 */
const scrape = (tribes) => {
	
};
