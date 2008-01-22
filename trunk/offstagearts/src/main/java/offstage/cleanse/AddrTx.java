/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2007 by Robert Fischer

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/**
 * Address Standardization Solution, PHP Edition.
 *
 * <p>Aids processing of United States postal address data.</p>
 *
 * <p>Takes a Delivery Address Line entered by a user and reformats it to
 * conform with the United States Postal Service's Addressing Standards.</p>
 *
 * <p>The class also contains a state list generator for use in XHTML forms.</p>
 *
 * <p>Requires PHP 4 or later.</p>
 *
 * <p>Address Standardization Solution is a trademark of The Analysis
 * and Solutions Company.</p>
 *
 * <pre>
 * ======================================================================
 * SIMPLE PUBLIC LICENSE                        VERSION 1.1   2003-01-21
 *
 * Copyright (c) The Analysis and Solutions Company
 * http://www.analysisandsolutions.com/
 *
 * 1.  Permission to use, copy, modify, and distribute this software and
 * its documentation, with or without modification, for any purpose and
 * without fee or royalty is hereby granted, provided that you include
 * the following on ALL copies of the software and documentation or
 * portions thereof, including modifications, that you make:
 *
 *     a.  The full text of this license in a location viewable to users
 *     of the redistributed or derivative work.
 *
 *     b.  Notice of any changes or modifications to the files,
 *     including the date changes were made.
 *
 * 2.  The name, servicemarks and trademarks of the copyright holders
 * may NOT be used in advertising or publicity pertaining to the
 * software without specific, written prior permission.
 *
 * 3.  Title to copyright in this software and any associated
 * documentation will at all times remain with copyright holders.
 *
 * 4.  THIS SOFTWARE AND DOCUMENTATION IS PROVIDED "AS IS," AND
 * COPYRIGHT HOLDERS MAKE NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY
 * OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT THE USE OF THE SOFTWARE
 * OR DOCUMENTATION WILL NOT INFRINGE ANY THIRD PARTY PATENTS,
 * COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.
 *
 * 5.  COPYRIGHT HOLDERS WILL NOT BE LIABLE FOR ANY DAMAGES, INCLUDING
 * BUT NOT LIMITED TO, DIRECT, INDIRECT, SPECIAL OR CONSEQUENTIAL,
 * ARISING OUT OF ANY USE OF THE SOFTWARE OR DOCUMENTATION.
 * ======================================================================
 * </pre>
 *
 * @package    AddressStandardizationSolution
 * @author     Daniel Convissor <danielc@analysisandsolutions.com>, Robert Fischer
 * @copyright  The Analysis and Solutions Company, 2001-2006; Robert Fischer, 2007
 * @version    $Name: rel-5-5 $ $Id: addr-tx.inc,v 5.7 2006/03/18 17:46:08 danielc Exp $
 * @link       http://www.analysisandsolutions.com/software/addr/addr.htm
 */


/**
 * Aids processing of United States postal address data.
 *
 * <p>Please consider making a contribution to support our open source
 * development. See the link below.</p>
 *
 * @package    AddressStandardizationSolution
 * @author     Daniel Convissor <danielc@analysisandsolutions.com> and Robert Fischer
 * @copyright  The Analysis and Solutions Company, 2001-2006; Robert Fischer, 2007
 * @version    $Name: rel-5-5 $ (Java)
 * @link       http://www.analysisandsolutions.com/software/addr/addr.htm
 * @link       http://www.analysisandsolutions.com/contribute/
 * @license    http://www.analysisandsolutions.com/software/license.htm Simple Public License
 */
package offstage.cleanse;

import java.util.*;
import java.util.regex.*;

public class AddrTx
{

static Map<String,String> States, Suffixes, Identifiers, Directionals, Numbers;

	public Map<String, String> getDirectionals()
	{
		return Directionals;
	}

static Map newMap(String[] mm)
{
	Map map = new HashMap();
	for (int i=0; i<mm.length; i += 2) {
		map.put(mm[i+0], mm[i+1]);
	}
	return map;
}

static {
	Directionals = newMap(new String[] {
		"E", "E",
		"EAST", "E",
		"E-R", "EAST",
		"N", "N",
		"NO", "N",
		"NORTH", "N",
		"N-R", "NORTH",
		"NE", "NE",
		"NORTHEAST", "NE",
		"NE-R", "NORTHEAST",
		"NORTHWEST", "NW",
		"NW-R", "NORTHWEST",
		"NW", "NW",
		"S", "S",
		"SO", "S",
		"SOUTH", "S",
		"S-R", "SOUTH",
		"SE", "SE",
		"SOUTHEAST", "SE",
		"SE-R", "SOUTHEAST",
		"SOUTHWEST", "SW",
		"SW-R", "SOUTHWEST",
		"SW", "SW",
		"W", "W",
		"WEST", "W",
		"W-R", "WEST",
	});

	Numbers = newMap(new String[] {
		"FIRST", "1",
		"ONE", "1",
		"TEN", "10",
		"TENTH", "10",
		"ELEVEN", "11",
		"ELEVENTH", "11",
		"TWELFTH", "12",
		"TWELVE", "12",
		"THIRTEEN", "13",
		"THIRTEENTH", "13",
		"FOURTEEN", "14",
		"FOURTEENTH", "14",
		"FIFTEEN", "15",
		"FIFTEENTH", "15",
		"SIXTEEN", "16",
		"SIXTEENTH", "16",
		"SEVENTEEN", "17",
		"SEVENTEENTH", "17",
		"EIGHTEEN", "18",
		"EIGHTEENTH", "18",
		"NINETEEN", "19",
		"NINETEENTH", "19",
		"SECOND", "2",
		"TWO", "2",
		"TWENTIETH", "20",
		"TWENTY", "20",
		"THIRD", "3",
		"THREE", "3",
		"FOUR", "4",
		"FOURTH", "4",
		"FIFTH", "5",
		"FIVE", "5",
		"SIX", "6",
		"SIXTH", "6",
		"SEVEN", "7",
		"SEVENTH", "7",
		"EIGHT", "8",
		"EIGHTH", "8",
		"NINE", "9",
		"NINTH", "9",
	});

	States = newMap(new String[] {
		"ARMED FORCES AMERICA", "AA",
		"ARMED FORCES EUROPE", "AE",
		"ALASKA", "AK",
		"ALABAMA", "AL",
		"ARMED FORCES PACIFIC", "AP",
		"ARKANSAS", "AR",
		"ARIZONA", "AZ",
		"CALIFORNIA", "CA",
		"COLORADO", "CO",
		"CONNECTICUT", "CT",
		"DISTRICT OF COLUMBIA", "DC",
		"DELAWARE", "DE",
		"FLORIDA", "FL",
		"GEORGIA", "GA",
		"HAWAII", "HI",
		"IOWA", "IA",
		"IDAHO", "ID",
		"ILLINOIS", "IL",
		"INDIANA", "IN",
		"KANSAS", "KS",
		"KENTUCKY", "KY",
		"LOUISIANA", "LA",
		"MASSACHUSETTS", "MA",
		"MARYLAND", "MD",
		"MAINE", "ME",
		"MICHIGAN", "MI",
		"MINNESOTA", "MN",
		"MISSOURI", "MO",
		"MISSISSIPPI", "MS",
		"MONTANA", "MT",
		"NORTH CAROLINA", "NC",
		"NORTH DAKOTA", "ND",
		"NEBRASKA", "NE",
		"NEW HAMPSHIRE", "NH",
		"NEW JERSEY", "NJ",
		"NEW MEXICO", "NM",
		"NEVADA", "NV",
		"NEW YORK", "NY",
		"OHIO", "OH",
		"OKLAHOMA", "OK",
		"OREGON", "OR",
		"PENNSYLVANIA", "PA",
		"RHODE ISLAND", "RI",
		"SOUTH CAROLINA", "SC",
		"SOUTH DAKOTA", "SD",
		"TENNESSEE", "TN",
		"TEXAS", "TX",
		"UTAH", "UT",
		"VIRGINIA", "VA",
		"VERMONT", "VT",
		"WASHINGTON", "WA",
		"WISCONSIN", "WI",
		"WEST VIRGINIA", "WV",
		"WYOMING", "WY",
	});

	Identifiers = newMap(new String[] {
		"APARTMENT", "APT",
		"APT-R", "APARTMENT",
		"APT", "APT",
		"BLDG", "BLDG",
		"BUILDING", "BLDG",
		"BLDG-R", "BUILDING",
		"BOX", "BOX",
		"BOX-R", "BOX",
		"BASEMENT", "BSMT",
		"BSMT-R", "BASEMENT",
		"BSMT", "BSMT",
		"DEPARTMENT", "DEPT",
		"DEPT-R", "DEPARTMENT",
		"DEPT", "DEPT",
		"FL", "FL",
		"FLOOR", "FL",
		"FL-R", "FLOOR",
		"FRNT", "FRNT",
		"FRONT", "FRNT",
		"FRNT-R", "FRONT",
		"HANGER", "HNGR",
		"HNGR-R", "HANGER",
		"HNGR", "HNGR",
		"KEY", "KEY",
		"KEY-R", "KEY",
		"LBBY", "LBBY",
		"LOBBY", "LBBY",
		"LBBY-R", "LOBBY",
		"LOT", "LOT",
		"LOT-R", "LOT",
		"LOWER", "LOWR",
		"LOWR-R", "LOWER",
		"LOWR", "LOWR",
		"OFC", "OFC",
		"OFFICE", "OFC",
		"OFC-R", "OFFICE",
		"PENTHOUSE", "PH",
		"PH-R", "PENTHOUSE",
		"PH", "PH",
		"PIER", "PIER",
		"PIER-R", "PIER",
		"PMB", "PMB",
		"PMB-R", "PMB",
		"REAR", "REAR",
		"REAR-R", "REAR",
		"RM", "RM",
		"ROOM", "RM",
		"RM-R", "ROOM",
		"SIDE", "SIDE",
		"SIDE-R", "SIDE",
		"SLIP", "SLIP",
		"SLIP-R", "SLIP",
		"SPACE", "SPC",
		"SPC-R", "SPACE",
		"SPC", "SPC",
		"STE", "STE",
		"SUITE", "STE",
		"STE-R", "SUITE",
		"STOP", "STOP",
		"STOP-R", "STOP",
		"TRAILER", "TRLR",
		"TRLR-R", "TRAILER",
		"TRLR", "TRLR",
		"UNIT", "UNIT",
		"UNIT-R", "UNIT",
		"UPPER", "UPPR",
		"UPPR-R", "UPPER",
		"UPPR", "UPPR",
		"UPR", "UPPR",
	});

	Suffixes = newMap(new String[] {
		"ALLEE", "ALY",
		"ALLEY", "ALY",
		"ALY-R", "ALLEY",
		"ALLY", "ALY",
		"ALY", "ALY",
		"ANEX", "ANX",
		"ANNEX", "ANX",
		"ANX-R", "ANNEX",
		"ANNX", "ANX",
		"ANX", "ANX",
		"ARC", "ARC",
		"ARCADE", "ARC",
		"ARC-R", "ARCADE",
		"AV", "AVE",
		"AVE", "AVE",
		"AVEN", "AVE",
		"AVENU", "AVE",
		"AVENUE", "AVE",
		"AVE-R", "AVENUE",
		"AVN", "AVE",
		"AVNUE", "AVE",
		"BCH", "BCH",
		"BEACH", "BCH",
		"BCH-R", "BEACH",
		"BG", "BG",
		"BURG", "BG",
		"BG-R", "BURG",
		"BGS", "BGS",
		"BURGS", "BGS",
		"BGS-R", "BURGS",
		"BLF", "BLF",
		"BLUF", "BLF",
		"BLUFF", "BLF",
		"BLF-R", "BLUFF",
		"BLFS", "BLFS",
		"BLUFFS", "BLFS",
		"BLFS-R", "BLUFFS",
		"BLVD", "BLVD",
		"BLVRD", "BLVD",
		"BOUL", "BLVD",
		"BOULEVARD", "BLVD",
		"BLVD-R", "BOULEVARD",
		"BOULOVARD", "BLVD",
		"BOULV", "BLVD",
		"BOULVRD", "BLVD",
		"BULAVARD", "BLVD",
		"BULEVARD", "BLVD",
		"BULLEVARD", "BLVD",
		"BULOVARD", "BLVD",
		"BULVD", "BLVD",
		"BEND", "BND",
		"BND-R", "BEND",
		"BND", "BND",
		"BR", "BR",
		"BRANCH", "BR",
		"BR-R", "BRANCH",
		"BRNCH", "BR",
		"BRDGE", "BRG",
		"BRG", "BRG",
		"BRGE", "BRG",
		"BRIDGE", "BRG",
		"BRG-R", "BRIDGE",
		"BRK", "BRK",
		"BROOK", "BRK",
		"BRK-R", "BROOK",
		"BRKS", "BRKS",
		"BROOKS", "BRKS",
		"BRKS-R", "BROOKS",
		"BOT", "BTM",
		"BOTTM", "BTM",
		"BOTTOM", "BTM",
		"BTM-R", "BOTTOM",
		"BTM", "BTM",
		"BYP", "BYP",
		"BYPA", "BYP",
		"BYPAS", "BYP",
		"BYPASS", "BYP",
		"BYP-R", "BYPASS",
		"BYPS", "BYP",
		"BAYOO", "BYU",
		"BAYOU", "BYU",
		"BYU-R", "BAYOU",
		"BYO", "BYU",
		"BYOU", "BYU",
		"BYU", "BYU",
		"CIR", "CIR",
		"CIRC", "CIR",
		"CIRCEL", "CIR",
		"CIRCL", "CIR",
		"CIRCLE", "CIR",
		"CIR-R", "CIRCLE",
		"CRCL", "CIR",
		"CRCLE", "CIR",
		"CIRCELS", "CIRS",
		"CIRCLES", "CIRS",
		"CIRS-R", "CIRCLES",
		"CIRCLS", "CIRS",
		"CIRCS", "CIRS",
		"CIRS", "CIRS",
		"CRCLES", "CIRS",
		"CRCLS", "CIRS",
		"CLB", "CLB",
		"CLUB", "CLB",
		"CLB-R", "CLUB",
		"CLF", "CLF",
		"CLIF", "CLF",
		"CLIFF", "CLF",
		"CLF-R", "CLIFF",
		"CLFS", "CLFS",
		"CLIFFS", "CLFS",
		"CLFS-R", "CLIFFS",
		"CLIFS", "CLFS",
		"CMN", "CMN",
		"COMMON", "CMN",
		"CMN-R", "COMMON",
		"COMN", "CMN",
		"COR", "COR",
		"CORN", "COR",
		"CORNER", "COR",
		"COR-R", "CORNER",
		"CRNR", "COR",
		"CORNERS", "CORS",
		"CORS-R", "CORNERS",
		"CORNRS", "CORS",
		"CORS", "CORS",
		"CRNRS", "CORS",
		"CAMP", "CP",
		"CP-R", "CAMP",
		"CMP", "CP",
		"CP", "CP",
		"CAPE", "CPE",
		"CPE-R", "CAPE",
		"CPE", "CPE",
		"CRECENT", "CRES",
		"CRES", "CRES",
		"CRESCENT", "CRES",
		"CRES-R", "CRESCENT",
		"CRESENT", "CRES",
		"CRSCNT", "CRES",
		"CRSENT", "CRES",
		"CRSNT", "CRES",
		"CK", "CRK",
		"CR", "CRK",
		"CREEK", "CRK",
		"CRK-R", "CREEK",
		"CREK", "CRK",
		"CRK", "CRK",
		"COARSE", "CRSE",
		"COURSE", "CRSE",
		"CRSE-R", "COURSE",
		"CRSE", "CRSE",
		"CREST", "CRST",
		"CRST-R", "CREST",
		"CRST", "CRST",
		"CAUSEWAY", "CSWY",
		"CSWY-R", "CAUSEWAY",
		"CAUSEWY", "CSWY",
		"CAUSWAY", "CSWY",
		"CAUSWY", "CSWY",
		"CSWY", "CSWY",
		"CORT", "CT",
		"COURT", "CT",
		"CT-R", "COURT",
		"CRT", "CT",
		"CT", "CT",
		"CEN", "CTR",
		"CENT", "CTR",
		"CENTER", "CTR",
		"CTR-R", "CENTER",
		"CENTR", "CTR",
		"CENTRE", "CTR",
		"CNTER", "CTR",
		"CNTR", "CTR",
		"CTR", "CTR",
		"CENS", "CTRS",
		"CENTERS", "CTRS",
		"CTRS-R", "CENTERS",
		"CENTRES", "CTRS",
		"CENTRS", "CTRS",
		"CENTS", "CTRS",
		"CNTERS", "CTRS",
		"CNTRS", "CTRS",
		"CTRS", "CTRS",
		"COURTS", "CTS",
		"CTS-R", "COURTS",
		"CTS", "CTS",
		"CRV", "CURV",
		"CURV", "CURV",
		"CURVE", "CURV",
		"CURV-R", "CURVE",
		"COV", "CV",
		"COVE", "CV",
		"CV-R", "COVE",
		"CV", "CV",
		"COVES", "CVS",
		"CVS-R", "COVES",
		"COVS", "CVS",
		"CVS", "CVS",
		"CAN", "CYN",
		"CANYN", "CYN",
		"CANYON", "CYN",
		"CYN-R", "CANYON",
		"CNYN", "CYN",
		"CYN", "CYN",
		"DAL", "DL",
		"DALE", "DL",
		"DL-R", "DALE",
		"DL", "DL",
		"DAM", "DM",
		"DM-R", "DAM",
		"DM", "DM",
		"DR", "DR",
		"DRIV", "DR",
		"DRIVE", "DR",
		"DR-R", "DRIVE",
		"DRV", "DR",
		"DRIVES", "DRS",
		"DRS-R", "DRIVES",
		"DRIVS", "DRS",
		"DRS", "DRS",
		"DRVS", "DRS",
		"DIV", "DV",
		"DIVD", "DV",
		"DIVID", "DV",
		"DIVIDE", "DV",
		"DV-R", "DIVIDE",
		"DV", "DV",
		"DVD", "DV",
		"EST", "EST",
		"ESTA", "EST",
		"ESTATE", "EST",
		"EST-R", "ESTATE",
		"ESTAS", "ESTS",
		"ESTATES", "ESTS",
		"ESTS-R", "ESTATES",
		"ESTS", "ESTS",
		"EXP", "EXPY",
		"EXPR", "EXPY",
		"EXPRESS", "EXPY",
		"EXPRESSWAY", "EXPY",
		"EXPY-R", "EXPRESSWAY",
		"EXPRESWAY", "EXPY",
		"EXPRSWY", "EXPY",
		"EXPRWY", "EXPY",
		"EXPW", "EXPY",
		"EXPWY", "EXPY",
		"EXPY", "EXPY",
		"EXWAY", "EXPY",
		"EXWY", "EXPY",
		"EXT", "EXT",
		"EXTEN", "EXT",
		"EXTENSION", "EXT",
		"EXT-R", "EXTENSION",
		"EXTENSN", "EXT",
		"EXTN", "EXT",
		"EXTNSN", "EXT",
		"EXTENS", "EXTS",
		"EXTENSIONS", "EXTS",
		"EXTS-R", "EXTENSIONS",
		"EXTENSNS", "EXTS",
		"EXTNS", "EXTS",
		"EXTNSNS", "EXTS",
		"EXTS", "EXTS",
		"FAL", "FALL",
		"FALL", "FALL",
		"FALL-R", "FALL",
		"FIELD", "FLD",
		"FLD-R", "FIELD",
		"FLD", "FLD",
		"FIELDS", "FLDS",
		"FLDS-R", "FIELDS",
		"FLDS", "FLDS",
		"FALLS", "FLS",
		"FLS-R", "FALLS",
		"FALS", "FLS",
		"FLS", "FLS",
		"FLAT", "FLT",
		"FLT-R", "FLAT",
		"FLT", "FLT",
		"FLATS", "FLTS",
		"FLTS-R", "FLATS",
		"FLTS", "FLTS",
		"FORD", "FRD",
		"FRD-R", "FORD",
		"FRD", "FRD",
		"FORDS", "FRDS",
		"FRDS-R", "FORDS",
		"FRDS", "FRDS",
		"FORG", "FRG",
		"FORGE", "FRG",
		"FRG-R", "FORGE",
		"FRG", "FRG",
		"FORGES", "FRGS",
		"FRGS-R", "FORGES",
		"FRGS", "FRGS",
		"FORK", "FRK",
		"FRK-R", "FORK",
		"FRK", "FRK",
		"FORKS", "FRKS",
		"FRKS-R", "FORKS",
		"FRKS", "FRKS",
		"FOREST", "FRST",
		"FRST-R", "FOREST",
		"FORESTS", "FRST",
		"FORREST", "FRST",
		"FORRESTS", "FRST",
		"FORRST", "FRST",
		"FORRSTS", "FRST",
		"FORST", "FRST",
		"FORSTS", "FRST",
		"FRRESTS", "FRST",
		"FRRST", "FRST",
		"FRRSTS", "FRST",
		"FRST", "FRST",
		"FERRY", "FRY",
		"FRY-R", "FERRY",
		"FERY", "FRY",
		"FRRY", "FRY",
		"FRY", "FRY",
		"FORT", "FT",
		"FT-R", "FORT",
		"FRT", "FT",
		"FT", "FT",
		"FREEWAY", "FWY",
		"FWY-R", "FREEWAY",
		"FREEWY", "FWY",
		"FREWAY", "FWY",
		"FREWY", "FWY",
		"FRWAY", "FWY",
		"FRWY", "FWY",
		"FWY", "FWY",
		"GARDEN", "GDN",
		"GDN-R", "GARDEN",
		"GARDN", "GDN",
		"GDN", "GDN",
		"GRDEN", "GDN",
		"GRDN", "GDN",
		"GARDENS", "GDNS",
		"GDNS-R", "GARDENS",
		"GARDNS", "GDNS",
		"GDNS", "GDNS",
		"GRDENS", "GDNS",
		"GRDNS", "GDNS",
		"GLEN", "GLN",
		"GLN-R", "GLEN",
		"GLENN", "GLN",
		"GLN", "GLN",
		"GLENNS", "GLNS",
		"GLENS", "GLNS",
		"GLNS-R", "GLENS",
		"GLNS", "GLNS",
		"GREEN", "GRN",
		"GRN-R", "GREEN",
		"GREN", "GRN",
		"GRN", "GRN",
		"GREENS", "GRNS",
		"GRNS-R", "GREENS",
		"GRENS", "GRNS",
		"GRNS", "GRNS",
		"GROV", "GRV",
		"GROVE", "GRV",
		"GRV-R", "GROVE",
		"GRV", "GRV",
		"GROVES", "GRVS",
		"GRVS-R", "GROVES",
		"GROVS", "GRVS",
		"GRVS", "GRVS",
		"GATEWAY", "GTWY",
		"GTWY-R", "GATEWAY",
		"GATEWY", "GTWY",
		"GATWAY", "GTWY",
		"GTWAY", "GTWY",
		"GTWY", "GTWY",
		"HARB", "HBR",
		"HARBOR", "HBR",
		"HBR-R", "HARBOR",
		"HARBR", "HBR",
		"HBR", "HBR",
		"HRBOR", "HBR",
		"HARBORS", "HBRS",
		"HBRS-R", "HARBORS",
		"HBRS", "HBRS",
		"HILL", "HL",
		"HL-R", "HILL",
		"HL", "HL",
		"HILLS", "HLS",
		"HLS-R", "HILLS",
		"HLS", "HLS",
		"HLLW", "HOLW",
		"HLLWS", "HOLW",
		"HOLLOW", "HOLW",
		"HOLW-R", "HOLLOW",
		"HOLLOWS", "HOLW",
		"HOLOW", "HOLW",
		"HOLOWS", "HOLW",
		"HOLW", "HOLW",
		"HOLWS", "HOLW",
		"HEIGHT", "HTS",
		"HEIGHTS", "HTS",
		"HTS-R", "HEIGHTS",
		"HGTS", "HTS",
		"HT", "HTS",
		"HTS", "HTS",
		"HAVEN", "HVN",
		"HVN-R", "HAVEN",
		"HAVN", "HVN",
		"HVN", "HVN",
		"HIGHWAY", "HWY",
		"HWY-R", "HIGHWAY",
		"HIGHWY", "HWY",
		"HIWAY", "HWY",
		"HIWY", "HWY",
		"HWAY", "HWY",
		"HWY", "HWY",
		"HYGHWAY", "HWY",
		"HYWAY", "HWY",
		"HYWY", "HWY",
		"INLET", "INLT",
		"INLT-R", "INLET",
		"INLT", "INLT",
		"ILAND", "IS",
		"ILND", "IS",
		"IS", "IS",
		"ISLAND", "IS",
		"IS-R", "ISLAND",
		"ISLND", "IS",
		"ILE", "ISLE",
		"ISLE", "ISLE",
		"ISLE-R", "ISLE",
		"ISLES", "ISLE",
		"ILANDS", "ISS",
		"ILNDS", "ISS",
		"ISLANDS", "ISS",
		"ISS-R", "ISLANDS",
		"ISLDS", "ISS",
		"ISLNDS", "ISS",
		"ISS", "ISS",
		"JCT", "JCT",
		"JCTION", "JCT",
		"JCTN", "JCT",
		"JUNCTION", "JCT",
		"JCT-R", "JUNCTION",
		"JUNCTN", "JCT",
		"JUNCTON", "JCT",
		"JCTIONS", "JCTS",
		"JCTNS", "JCTS",
		"JCTS", "JCTS",
		"JUNCTIONS", "JCTS",
		"JCTS-R", "JUNCTIONS",
		"JUNCTONS", "JCTS",
		"JUNGTNS", "JCTS",
		"KNL", "KNL",
		"KNOL", "KNL",
		"KNOLL", "KNL",
		"KNL-R", "KNOLL",
		"KNLS", "KNLS",
		"KNOLLS", "KNLS",
		"KNLS-R", "KNOLLS",
		"KNOLS", "KNLS",
		"KEY", "KY",
		"KY-R", "KEY",
		"KY", "KY",
		"KEYS", "KYS",
		"KYS-R", "KEYS",
		"KYS", "KYS",
		"LAND", "LAND",
		"LAND-R", "LAND",
		"LCK", "LCK",
		"LOCK", "LCK",
		"LCK-R", "LOCK",
		"LCKS", "LCKS",
		"LOCKS", "LCKS",
		"LCKS-R", "LOCKS",
		"LDG", "LDG",
		"LDGE", "LDG",
		"LODG", "LDG",
		"LODGE", "LDG",
		"LDG-R", "LODGE",
		"LF", "LF",
		"LOAF", "LF",
		"LF-R", "LOAF",
		"LGT", "LGT",
		"LIGHT", "LGT",
		"LGT-R", "LIGHT",
		"LT", "LGT",
		"LGTS", "LGTS",
		"LIGHTS", "LGTS",
		"LGTS-R", "LIGHTS",
		"LTS", "LGTS",
		"LAKE", "LK",
		"LK-R", "LAKE",
		"LK", "LK",
		"LAKES", "LKS",
		"LKS-R", "LAKES",
		"LKS", "LKS",
		"LA", "LN",
		"LANE", "LN",
		"LN-R", "LANE",
		"LANES", "LN",
		"LN", "LN",
		"LNS", "LN",
		"LANDG", "LNDG",
		"LANDING", "LNDG",
		"LNDG-R", "LANDING",
		"LANDNG", "LNDG",
		"LNDG", "LNDG",
		"LNDNG", "LNDG",
		"LOOP", "LOOP",
		"LOOP-R", "LOOP",
		"LOOPS", "LOOP",
		"MALL", "MALL",
		"MALL-R", "MALL",
		"MDW", "MDW",
		"MEADOW", "MDW",
		"MDW-R", "MEADOW",
		"MDWS", "MDWS",
		"MEADOWS", "MDWS",
		"MDWS-R", "MEADOWS",
		"MEDOWS", "MDWS",
		"MEDWS", "MDWS",
		"MEWS", "MEWS",
		"MEWS-R", "MEWS",
		"MIL", "ML",
		"MILL", "ML",
		"ML-R", "MILL",
		"ML", "ML",
		"MILLS", "MLS",
		"MLS-R", "MILLS",
		"MILS", "MLS",
		"MLS", "MLS",
		"MANOR", "MNR",
		"MNR-R", "MANOR",
		"MANR", "MNR",
		"MNR", "MNR",
		"MANORS", "MNRS",
		"MNRS-R", "MANORS",
		"MANRS", "MNRS",
		"MNRS", "MNRS",
		"MISN", "MSN",
		"MISSION", "MSN",
		"MSN-R", "MISSION",
		"MISSN", "MSN",
		"MSN", "MSN",
		"MSSN", "MSN",
		"MNT", "MT",
		"MOUNT", "MT",
		"MT-R", "MOUNT",
		"MT", "MT",
		"MNTAIN", "MTN",
		"MNTN", "MTN",
		"MOUNTAIN", "MTN",
		"MTN-R", "MOUNTAIN",
		"MOUNTIN", "MTN",
		"MTIN", "MTN",
		"MTN", "MTN",
		"MNTNS", "MTNS",
		"MOUNTAINS", "MTNS",
		"MTNS-R", "MOUNTAINS",
		"MTNS", "MTNS",
		"MOTORWAY", "MTWY",
		"MTWY-R", "MOTORWAY",
		"MOTORWY", "MTWY",
		"MOTRWY", "MTWY",
		"MOTWY", "MTWY",
		"MTRWY", "MTWY",
		"MTWY", "MTWY",
		"NCK", "NCK",
		"NECK", "NCK",
		"NCK-R", "NECK",
		"NEK", "NCK",
		"OPAS", "OPAS",
		"OVERPAS", "OPAS",
		"OVERPASS", "OPAS",
		"OPAS-R", "OVERPASS",
		"OVERPS", "OPAS",
		"OVRPS", "OPAS",
		"ORCH", "ORCH",
		"ORCHARD", "ORCH",
		"ORCH-R", "ORCHARD",
		"ORCHRD", "ORCH",
		"OVAL", "OVAL",
		"OVAL-R", "OVAL",
		"OVL", "OVAL",
		"PARK", "PARK",
		"PARK-R", "PARK",
		"PARKS", "PARK",
		"PK", "PARK",
		"PRK", "PARK",
		"PAS", "PASS",
		"PASS", "PASS",
		"PASS-R", "PASS",
		"PATH", "PATH",
		"PATH-R", "PATH",
		"PATHS", "PATH",
		"PIKE", "PIKE",
		"PIKE-R", "PIKE",
		"PIKES", "PIKE",
		"PARKWAY", "PKWY",
		"PKWY-R", "PARKWAY",
		"PARKWAYS", "PKWY",
		"PARKWY", "PKWY",
		"PKWAY", "PKWY",
		"PKWY", "PKWY",
		"PKWYS", "PKWY",
		"PKY", "PKWY",
		"PL", "PL",
		"PLAC", "PL",
		"PLACE", "PL",
		"PL-R", "PLACE",
		"PLASE", "PL",
		"PLAIN", "PLN",
		"PLN-R", "PLAIN",
		"PLN", "PLN",
		"PLAINES", "PLNS",
		"PLAINS", "PLNS",
		"PLNS-R", "PLAINS",
		"PLNS", "PLNS",
		"PLAZ", "PLZ",
		"PLAZA", "PLZ",
		"PLZ-R", "PLAZA",
		"PLZ", "PLZ",
		"PLZA", "PLZ",
		"PZ", "PLZ",
		"PINE", "PNE",
		"PNE-R", "PINE",
		"PNE", "PNE",
		"PINES", "PNES",
		"PNES-R", "PINES",
		"PNES", "PNES",
		"PR", "PR",
		"PRAIR", "PR",
		"PRAIRIE", "PR",
		"PR-R", "PRAIRIE",
		"PRARE", "PR",
		"PRARIE", "PR",
		"PRR", "PR",
		"PRRE", "PR",
		"PORT", "PRT",
		"PRT-R", "PORT",
		"PRT", "PRT",
		"PORTS", "PRTS",
		"PRTS-R", "PORTS",
		"PRTS", "PRTS",
		"PASG", "PSGE",
		"PASSAGE", "PSGE",
		"PSGE-R", "PASSAGE",
		"PASSG", "PSGE",
		"PSGE", "PSGE",
		"PNT", "PT",
		"POINT", "PT",
		"PT-R", "POINT",
		"PT", "PT",
		"PNTS", "PTS",
		"POINTS", "PTS",
		"PTS-R", "POINTS",
		"PTS", "PTS",
		"RAD", "RADL",
		"RADIAL", "RADL",
		"RADL-R", "RADIAL",
		"RADIEL", "RADL",
		"RADL", "RADL",
		"RAMP", "RAMP",
		"RAMP-R", "RAMP",
		"RD", "RD",
		"ROAD", "RD",
		"RD-R", "ROAD",
		"RDG", "RDG",
		"RDGE", "RDG",
		"RIDGE", "RDG",
		"RDG-R", "RIDGE",
		"RDGS", "RDGS",
		"RIDGES", "RDGS",
		"RDGS-R", "RIDGES",
		"RDS", "RDS",
		"ROADS", "RDS",
		"RDS-R", "ROADS",
		"RIV", "RIV",
		"RIVER", "RIV",
		"RIV-R", "RIVER",
		"RIVR", "RIV",
		"RVR", "RIV",
		"RANCH", "RNCH",
		"RNCH-R", "RANCH",
		"RANCHES", "RNCH",
		"RNCH", "RNCH",
		"RNCHS", "RNCH",
		"RAOD", "ROAD",
		"ROW", "ROW",
		"ROW-R", "ROW",
		"RAPID", "RPD",
		"RPD-R", "RAPID",
		"RPD", "RPD",
		"RAPIDS", "RPDS",
		"RPDS-R", "RAPIDS",
		"RPDS", "RPDS",
		"REST", "RST",
		"RST-R", "REST",
		"RST", "RST",
		"ROUTE", "RTE",
		"RTE-R", "ROUTE",
		"RT", "RTE",
		"RTE", "RTE",
		"RUE", "RUE",
		"RUE-R", "RUE",
		"RUN", "RUN",
		"RUN-R", "RUN",
		"SHL", "SHL",
		"SHOAL", "SHL",
		"SHL-R", "SHOAL",
		"SHOL", "SHL",
		"SHLS", "SHLS",
		"SHOALS", "SHLS",
		"SHLS-R", "SHOALS",
		"SHOLS", "SHLS",
		"SHOAR", "SHR",
		"SHORE", "SHR",
		"SHR-R", "SHORE",
		"SHR", "SHR",
		"SHOARS", "SHRS",
		"SHORES", "SHRS",
		"SHRS-R", "SHORES",
		"SHRS", "SHRS",
		"SKWY", "SKWY",
		"SKYWAY", "SKWY",
		"SKWY-R", "SKYWAY",
		"SKYWY", "SKWY",
		"SMT", "SMT",
		"SUMIT", "SMT",
		"SUMITT", "SMT",
		"SUMMIT", "SMT",
		"SMT-R", "SUMMIT",
		"SUMT", "SMT",
		"SPG", "SPG",
		"SPNG", "SPG",
		"SPRING", "SPG",
		"SPG-R", "SPRING",
		"SPRNG", "SPG",
		"SPGS", "SPGS",
		"SPNGS", "SPGS",
		"SPRINGS", "SPGS",
		"SPGS-R", "SPRINGS",
		"SPRNGS", "SPGS",
		"SPR", "SPUR",
		"SPRS", "SPUR",
		"SPUR", "SPUR",
		"SPUR-R", "SPUR",
		"SPURS", "SPUR",
		"SQ", "SQ",
		"SQAR", "SQ",
		"SQR", "SQ",
		"SQRE", "SQ",
		"SQU", "SQ",
		"SQUARE", "SQ",
		"SQ-R", "SQUARE",
		"SQARS", "SQS",
		"SQRS", "SQS",
		"SQS", "SQS",
		"SQUARES", "SQS",
		"SQS-R", "SQUARES",
		"ST", "ST",
		"STR", "ST",
		"STREET", "ST",
		"ST-R", "STREET",
		"STRT", "ST",
		"STA", "STA",
		"STATION", "STA",
		"STA-R", "STATION",
		"STATN", "STA",
		"STN", "STA",
		"STRA", "STRA",
		"STRAV", "STRA",
		"STRAVE", "STRA",
		"STRAVEN", "STRA",
		"STRAVENUE", "STRA",
		"STRA-R", "STRAVENUE",
		"STRAVN", "STRA",
		"STRVN", "STRA",
		"STRVNUE", "STRA",
		"STREAM", "STRM",
		"STRM-R", "STREAM",
		"STREME", "STRM",
		"STRM", "STRM",
		"STREETS", "STS",
		"STS-R", "STREETS",
		"STS", "STS",
		"TER", "TER",
		"TERACE", "TER",
		"TERASE", "TER",
		"TERR", "TER",
		"TERRACE", "TER",
		"TER-R", "TERRACE",
		"TERRASE", "TER",
		"TERRC", "TER",
		"TERRICE", "TER",
		"TPK", "TPKE",
		"TPKE", "TPKE",
		"TRNPK", "TPKE",
		"TRPK", "TPKE",
		"TURNPIKE", "TPKE",
		"TPKE-R", "TURNPIKE",
		"TURNPK", "TPKE",
		"TRACK", "TRAK",
		"TRAK-R", "TRACK",
		"TRACKS", "TRAK",
		"TRAK", "TRAK",
		"TRK", "TRAK",
		"TRKS", "TRAK",
		"TRACE", "TRCE",
		"TRCE-R", "TRACE",
		"TRACES", "TRCE",
		"TRCE", "TRCE",
		"TRAFFICWAY", "TRFY",
		"TRFY-R", "TRAFFICWAY",
		"TRAFFICWY", "TRFY",
		"TRAFWAY", "TRFY",
		"TRFCWY", "TRFY",
		"TRFFCWY", "TRFY",
		"TRFFWY", "TRFY",
		"TRFWY", "TRFY",
		"TRFY", "TRFY",
		"TR", "TRL",
		"TRAIL", "TRL",
		"TRL-R", "TRAIL",
		"TRAILS", "TRL",
		"TRL", "TRL",
		"TRLS", "TRL",
		"THROUGHWAY", "TRWY",
		"TRWY-R", "THROUGHWAY",
		"THROUGHWY", "TRWY",
		"THRUWAY", "TRWY",
		"THRUWY", "TRWY",
		"THRWAY", "TRWY",
		"THRWY", "TRWY",
		"THWY", "TRWY",
		"TRWY", "TRWY",
		"TUNEL", "TUNL",
		"TUNL", "TUNL",
		"TUNLS", "TUNL",
		"TUNNEL", "TUNL",
		"TUNL-R", "TUNNEL",
		"TUNNELS", "TUNL",
		"TUNNL", "TUNL",
		"UN", "UN",
		"UNION", "UN",
		"UN-R", "UNION",
		"UNIONS", "UNS",
		"UNS-R", "UNIONS",
		"UNS", "UNS",
		"UDRPS", "UPAS",
		"UNDERPAS", "UPAS",
		"UNDERPASS", "UPAS",
		"UPAS-R", "UNDERPASS",
		"UNDERPS", "UPAS",
		"UNDRPAS", "UPAS",
		"UNDRPS", "UPAS",
		"UPAS", "UPAS",
		"VDCT", "VIA",
		"VIA", "VIA",
		"VIADCT", "VIA",
		"VIADUCT", "VIA",
		"VIA-R", "VIADUCT",
		"VIS", "VIS",
		"VIST", "VIS",
		"VISTA", "VIS",
		"VIS-R", "VISTA",
		"VST", "VIS",
		"VSTA", "VIS",
		"VILLE", "VL",
		"VL-R", "VILLE",
		"VL", "VL",
		"VILG", "VLG",
		"VILL", "VLG",
		"VILLAG", "VLG",
		"VILLAGE", "VLG",
		"VLG-R", "VILLAGE",
		"VILLG", "VLG",
		"VILLIAGE", "VLG",
		"VLG", "VLG",
		"VILGS", "VLGS",
		"VILLAGES", "VLGS",
		"VLGS-R", "VILLAGES",
		"VLGS", "VLGS",
		"VALLEY", "VLY",
		"VLY-R", "VALLEY",
		"VALLY", "VLY",
		"VALY", "VLY",
		"VLLY", "VLY",
		"VLY", "VLY",
		"VALLEYS", "VLYS",
		"VLYS-R", "VALLEYS",
		"VLYS", "VLYS",
		"VIEW", "VW",
		"VW-R", "VIEW",
		"VW", "VW",
		"VIEWS", "VWS",
		"VWS-R", "VIEWS",
		"VWS", "VWS",
		"WALK", "WALK",
		"WALK-R", "WALK",
		"WALKS", "WALK",
		"WLK", "WALK",
		"WALL", "WALL",
		"WALL-R", "WALL",
		"WAY", "WAY",
		"WAY-R", "WAY",
		"WY", "WAY",
		"WAYS", "WAYS",
		"WAYS-R", "WAYS",
		"WEL", "WL",
		"WELL", "WL",
		"WL-R", "WELL",
		"WL", "WL",
		"WELLS", "WLS",
		"WLS-R", "WELLS",
		"WELS", "WLS",
		"WLS", "WLS",
		"CROSING", "XING",
		"CROSNG", "XING",
		"CROSSING", "XING",
		"XING-R", "CROSSING",
		"CRSING", "XING",
		"CRSNG", "XING",
		"CRSSING", "XING",
		"CRSSNG", "XING",
		"XING", "XING",
		"CROSRD", "XRD",
		"CROSSRD", "XRD",
		"CROSSROAD", "XRD",
		"XRD-R", "CROSSROAD",
		"CRSRD", "XRD",
		"XRD", "XRD",
		"XROAD", "XRD",
	});
}

/**
 * Implement abbreviations for words at the ends of certain address lines.
 *
 * @param  string   $String  the address fragments to be analyzed
 * @return string   the cleaned up string
 */
static String ALS_EOL_Abbr(String line)
{
	int suff = 0;
	int id = 0;
	ArrayList<String> rout = new ArrayList();		// Reversed output
	
	String[] ll = line.split(" ");
	int Count = ll.length - 1;

	for (int i=Count; i>=0; --i) {
		String s;
		if ((s = Suffixes.get(ll[i])) != null) {
			if (suff != 0) {
				rout.add(s);
				++suff;
				if (i == Count) id=1;
			} else {
				rout.add(ll[i]);
			}
		} else if ((s = Identifiers.get(ll[i])) != null) {
			rout.add(s);
			id = 1;
		} else if ((s = Directionals.get(ll[i])) != null) {
			rout.add(s);
			if (i == Count) id=1;
		} else {
			rout.add(ll[i]);
		}
	}
	
	// implote(' ', reverse(out))
	StringBuffer out = new StringBuffer();
	for (int i=rout.size()-1; ; --i) {
		out.append(rout.get(i));
		if (i == 0) break;
		out.append(' ');
	}
	return out.toString();
}

static Map<String,Pattern> patCache = new HashMap();
static String ereg_replace(String regexp, String repl, String str)
{
	Pattern p = patCache.get(regexp);
	if (p == null) {
		p = Pattern.compile(regexp);
		patCache.put(regexp, p);
	}
	return p.matcher(str).replaceAll(repl);
}

static int ereg(String regexp, String str, String[] out)
{
	Pattern p = patCache.get(regexp);
	if (p == null) {
		p = Pattern.compile(regexp);
		patCache.put(regexp, p);
	}
	Matcher m = p.matcher(str);
	if (!m.matches()) return 0;
	
	// Construct parenthesis groupings
//	String[] out = new String[m.groupCount()+1];
	int n = m.groupCount() + 1;
	for (int i=0; i<n; ++i) out[i] = m.group(i);
	return m.groupCount();
}

static int ereg(String regexp, String str)
{
	Pattern p = patCache.get(regexp);
	if (p == null) {
		p = Pattern.compile(regexp);
		patCache.put(regexp, p);
	}
	Matcher m = p.matcher(str);
	if (!m.matches()) return 0;
	return m.groupCount();
}
static boolean empty(String s) { return s == null || "".equals(s); }

static String implode(String glue, String[] pieces)
{
	StringBuffer sb = new StringBuffer();
	for (int i=0; ;) {
//System.out.println("pieces[" + i + "] = " + pieces[i]);
		if (i == pieces.length) break;
		if (pieces[i] == null) {
			++i;
			continue;
		}
		sb.append(pieces[i]);
		if (++i == pieces.length) break;
		sb.append(glue);
	}
	return sb.toString();
}

static String substring(String s, int ix)
{
	if (ix < 0) {
		ix = s.length() + ix;
		if (ix < 0) ix = 0;
	}
	return s.substring(ix);
}

public static String AddressLineStandardization(String addr)
{
	String[] parts;
	
	if (addr == null || "".equals(addr)) return "";

	addr = ereg_replace("[#]", " # ", addr);
	addr = addr.trim().toUpperCase();
	addr = ereg_replace("[^A-Z0-9\\s/#.-]", "", addr);
	addr = ereg_replace("([A-Z])([.])", "$1 ", addr);
	addr = ereg_replace("([A-Z]+)([-])([A-Z]+)", "$1 $3", addr);
	addr = ereg_replace("([^A-Z]+)([/.-]+)([^0-9]+)", "$1$3", addr);
	addr = ereg_replace("([^0-9]+)([/.-]+)([^A-Z]+)", "$1$3", addr);
	addr = ereg_replace("([\\s]+)", " ", addr);
	String[] atom = new String[100];	// kludge, but simplifies conversion of PHP code
	String s;

	if (0!=ereg("(.+[\\s])([A-Z]+)([\\s][#])([\\s].+)", addr, atom)) {
		if (null!=(s=Identifiers.get(atom[2]))) {
			addr = atom[1] + atom[2] + atom[4];
		}
	}

	addr = addr.trim();

	parts = addr.split(" ");
	for (int i=0; i<parts.length; ++i) {
		String val = parts[i];
		if (null!=(s=Numbers.get(val))) parts[i] = s;
	}
	addr = implode(" ", parts);

	addr = ereg_replace("([0-9]+)(ST|ND|RD|TH)?([\\s]?)(FL|FLOOR|FLR)$", "FL $1", addr);
	addr = ereg_replace("(NORTH|SOUTH)([\\s])(EAST|WEST)", "$1$3", addr);

	if (0!=ereg("^(RR|RFD ROUTE|RURAL ROUTE|RURAL RT|RURAL RTE|RURAL DELIVERY|RD RTE|RD ROUTE)([\\s]?)([0-9]+)([A-Z #]+)([0-9A-Z]+)(.*)$", addr, atom)) {
		return "RR " + atom[3] + " BOX " + atom[5];
	}

	if (0!=ereg("^(BOX|BX)([ #]*)([0-9A-Z]+)([\\s])(RR|RFD ROUTE|RURAL ROUTE|RURAL RT|RURAL RTE|RURAL DELIVERY|RD RTE|RD ROUTE)([\\s]?)([0-9]+)(.*)$", addr, atom)) {
		return "RR " + atom[7] + " BOX " + atom[3];
	}


	if (0!=ereg("^(POST OFFICE BOX|PO BOX|P O|P O BOX|P O B|P O BX|POB|BOX|PO|PO BX|BX|FIRM CALLER|CALLER|BIN|LOCKBOX|DRAWER)([\\s]+([#][\\s])*)([0-9A-Z-]+)(.*)$", addr, atom)) {
		return "PO BOX " + atom[5];
	}

	if (0!=ereg("^([0-9A-Z.-]+[\\s]?[0-9/]*[\\s]?)(.*)( CNTY| COUNTY)([\\s])(HIGHWAY|HIGHWY|HIWAY|HIWY|HWAY|HWY)( NO | # | )?([0-9A-Z]+)(.*)$", addr, atom)) {
		if (null!=(s=States.get(atom[2]))) atom[2] = s;
		if (null!=(s=Identifiers.get(atom[7]))) {
			atom[7] = s;
			atom[8] = ereg_replace(" #", "", atom[8]);
			return atom[1] + atom[2] + " COUNTY HWY " + atom[7] + atom[8];
		}
		return atom[1] + atom[2] + " COUNTY HIGHWAY " + atom[7] + ALS_EOL_Abbr(atom[8]);
	}

	if (0!=ereg("^([0-9A-Z.-]+[\\s]?[0-9/]*[\\s]?)(.*)( CR |( CNTY| COUNTY)([\\s])(RD|ROAD))( NO | # | )?([0-9A-Z]+)(.*)$", addr, atom)) {
		if (null!=(s=States.get(atom[2]))) atom[2] = s;
		if (null!=(s=Identifiers.get(atom[8]))) {
			atom[8] = s;
			atom[9] = ereg_replace(" #", "", atom[9]);
			return atom[1] + atom[2] + " COUNTY RD " + atom[8] + atom[9];
		}
		return atom[1] + atom[2] + " COUNTY ROAD " + atom[8] + ALS_EOL_Abbr(atom[9]);
	}

	if (0!=ereg("^([0-9A-Z.-]+[\\s]?[0-9/]*[\\s]?)(.*)( SR|( ST| STATE)([\\s])(RD|ROAD))( NO | # | )?([0-9A-Z]+)(.*)$", addr, atom)) {
		if (null!=(s=States.get(atom[2]))) atom[2] = s;
		if (null!=(s=Identifiers.get(atom[8]))) {
			atom[8] = s;
			atom[9] = ereg_replace(" #", "", atom[9]);
			return atom[1] + atom[2] + " STATE RD " + atom[8] + atom[9];
		}
		return atom[1] + atom[2] + " STATE ROAD " + atom[8] + ALS_EOL_Abbr(atom[9]);
	}

	if (0!=ereg("^([0-9A-Z.-]+[\\s]?[0-9/]*[\\s]?)(.*)( ST| STATE)([\\s])(RT|RTE|ROUTE)( NO | # | )?([0-9A-Z]+)(.*)$", addr, atom)) {
		if (null!=(s=States.get(atom[2]))) atom[2] = s;
		if (null!=(s=Identifiers.get(atom[7]))) {
			atom[7] = s;
			atom[8] = ereg_replace(" #", "", atom[8]);
			return atom[1] + atom[2] + " STATE RTE " + atom[7] + atom[8];
		}
		return atom[1] + atom[2] + " STATE ROUTE " + atom[7] + ALS_EOL_Abbr(atom[8]);
	}

	if (0!=ereg("^([0-9A-Z.-]+[\\s][0-9/]*[\\s]?)(I|INTERSTATE|INTRST|INT)([\\s]?)(HIGHWAY|HIGHWY|HIWAY|HIWY|HWAY|HWY|H)?([\\s]?)([0-9]+)(.*)$", addr, atom)) {
		atom[7] = ereg_replace(" BYP ", " BYPASS ", atom[7]);
		return atom[1] + " INTERSTATE " + atom[6] + ALS_EOL_Abbr(atom[7]);
	}

	if (0!=ereg("^([0-9A-Z.-]+[\\s]?[0-9/]*[\\s]?)(.*)( ST| STATE)([\\s])(HIGHWAY|HIGHWY|HIWAY|HIWY|HWAY|HWY)( NO | # | )?([0-9A-Z]+)(.*)$", addr, atom)) {
		if (null!=(s=States.get(atom[2]))) atom[2] = s;
		if (null!=(s=Identifiers.get(atom[7]))) {
			atom[7] = s;
			atom[8] = ereg_replace(" #", "", atom[8]);
			return atom[1] + atom[2] +" STATE HWY " + atom[7] + atom[8];
		}
		return atom[1] + atom[2] + " STATE HIGHWAY " + atom[7] + ALS_EOL_Abbr(atom[8]);
	}

	if (0!=ereg("^([0-9A-Z.-]+[\\s]?[0-9/]*[\\s]?)(.*)( US| U S|UNITED STATES)([\\s])(HIGHWAY|HIGHWY|HIWAY|HIWY|HWAY|HWY)( NO | # | )?([0-9A-Z]+)(.*)$", addr, atom)) {
		if (null!=(s=States.get(atom[2]))) atom[2] = s;
		if (null!=(s=Identifiers.get(atom[7]))) {
			atom[7] = s;
			atom[8] = ereg_replace(" #", "", atom[8]);
			return atom[1] + atom[2] + " US HWY " + atom[7] + atom[8];
		}
		return atom[1] + atom[2] + " US HIGHWAY " + atom[7] + ALS_EOL_Abbr(atom[8]);
	}

	if (0!=ereg("^([0-9A-Z.-]+[\\s][0-9/]*[\\s]?)(RANCH )(RD|ROAD)( NO | # | )?([0-9A-Z]+)(.*)$", addr, atom)) {
		if (null!=(s=Identifiers.get(atom[5]))) {
			atom[5] = s;
			atom[6] = ereg_replace(" #", "", atom[6]);
			return atom[1] + "RANCH RD " + atom[5] + atom[6];
		}
		return atom[1] + "RANCH ROAD " + atom[5] + ALS_EOL_Abbr(atom[6]);
	}

	addr = ereg_replace("^([0-9A-Z.-]+)([\\s])([0-9][/][0-9])", "$1*$3", addr);

	if (0!=ereg("^([0-9A-Z/*.-]+[\\s])(RD|ROAD)([A-Z #]+)([0-9A-Z]+)(.*)$", addr, atom)) {
		atom[1] = ereg_replace("[*]", " ", atom[1]);
		return atom[1] + "ROAD " + atom[4] + ALS_EOL_Abbr(atom[5]);
	}

	if (0!=ereg("^([0-9A-Z/*.-]+[\\s])(RT|RTE|ROUTE)([A-Z #]+)([0-9A-Z]+)(.*)$", addr, atom)) {
		atom[1] = ereg_replace("[*]", " ", atom[1]);
		return atom[1] + "ROUTE " + atom[4] + ALS_EOL_Abbr(atom[5]);
	}

	if (0!=ereg("^([0-9A-Z/*.-]+[\\s])(AV|AVE|AVEN|AVENU|AVENUE|AVN|AVNUE)([\\s])([A-Z]+)(.*)$", addr, atom)) {
		atom[1] = ereg_replace("[*]", " ", atom[1]);
		return atom[1] + "AVENUE " + atom[4] + ALS_EOL_Abbr(atom[5]);
	}

	if (0!=ereg("^([0-9A-Z/*.-]+[\\s])(BLVD|BOUL|BOULEVARD|BOULV)([\\s])([A-Z]+)(.*)$", addr, atom)) {
		atom[1] = ereg_replace("[*]", " ", atom[1]);
		return atom[1] + "BOULEVARD " + ALS_EOL_Abbr(atom[4] + atom[5]);
	}

	addr = ereg_replace("^([0-9A-Z/*.-]+[\\s])(ST )", "$1SAINT ", addr);

	parts = addr.split(" ");
	int count = parts.length - 1;
	int suff  = 0;
	int id    = 0;
	String[] out = new String[100];
	
//System.out.println("addr = " + addr);
	for (int counter = count; counter > -1; counter--) {
//System.out.println("counter = " + counter);
//System.out.println("parts[counter] = " + parts[counter]);
		if (null!=(s=Suffixes.get(parts[counter]))) {
			if (suff != 0) {
				if (!empty(out[counter+1]) && !empty(out[counter+2])) {
					String ss = out[counter+1] + out[counter+2];
					if ("EAST W".equals(ss) || "WEST E".equals(ss) || "NORTH S".equals(ss) || "SOUTH N".equals(ss)) {
							out[counter] = parts[counter];
					} else {
						out[counter] = Suffixes.get(parts[counter]);
					}
				} else {
					out[counter] = Suffixes.get(parts[counter]);
				}
				if (counter == count) {
					id++;
				}

			} else {
				if (parts[counter].equals("VIA") || parts[counter].equals("LA")) {
						out[counter] = parts[counter];
				} else {
					out[counter] = Suffixes.get(parts[counter]);
					out[counter] = Suffixes.get(out[counter] + "-R");
				}
			}

			suff++;
		} else if (null!=(s=Identifiers.get(parts[counter]))) {
			out[counter] = s;
			if (suff > 0) {
				out[counter] = Identifiers.get(out[counter] + "-R");
			}
			id++;

		} else if (null!=(s=Directionals.get(parts[counter]))
				  && counter > 0
				  && counter < count) {
			int Prior = counter - 1;
			int Next = counter + 1;
			if (!empty(parts[Next])  &&  null!=(s=Suffixes.get(parts[Next]))) {
				out[counter] = Directionals.get(parts[counter]);
				if (suff <= 1) {
					out[counter] = Directionals.get("out[counter]-R");
				}

			} else if (counter > 2  &&  !empty(parts[Next])  &&  null!=(s=Directionals.get(parts[Next]))) {
				out[counter] = parts[counter];

			} else if (counter == 2  &&  null!=(s=Directionals.get(parts[Prior]))) {
				out[counter] = parts[counter];

			} else {
				out[counter] = Directionals.get(parts[counter]);
			}

			if (counter == count) {
				id = 1;
			}

		} else if (0!=ereg("(^([0-9]*)$)", parts[counter])
				  && counter > 0
				  && counter < count)
		{
			int Prior = counter - 1;
			int Next = counter + 1;
			if (null!=(s=Directionals.get(parts[Prior]))
				&& null!=(s=Directionals.get(parts[Next])))
			{
				out[counter] = parts[counter];
			} else {
				String ss = substring(parts[counter], -2);

				if (ss.equals("11") || ss.equals("12") || ss.equals("13")) {
					out[counter] = parts[counter] + "TH";
				} else {
					ss = parts[counter];
					char c = ss.charAt(ss.length()-1);
					
					switch (c) {
						case '1':
							out[counter] = parts[counter] + "ST";
							break;
						case '2':
							out[counter] = parts[counter] + "ND";
							break;
						case '3':
							out[counter] = parts[counter] + "RD";
							break;
						default:
							out[counter] = parts[counter] + "TH";
							break;
					}
				}
			}

		} else {
			out[counter] = parts[counter];
		}
	}
	out[0] = ereg_replace("[*]", " ", out[0]);
	return implode(" ", out);
}

public static void main(String[] args) {
	System.out.println("xxx " + AddressLineStandardization("12-23      south Wayland St. Northeast apartment 4"));
	System.out.println("xxx " + AddressLineStandardization("P O Box #3333"));
//	System.out.println("xxx " + AddressLineStandardization("norwell, ma   02222"));
}
}
