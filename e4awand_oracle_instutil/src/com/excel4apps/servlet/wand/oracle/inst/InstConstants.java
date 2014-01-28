package com.excel4apps.servlet.wand.oracle.inst;

import java.io.File;

/**
 * Wands Installer Constants Class
 * 
 * @author Andries Hanekom
 * 
 */
public final class InstConstants
{
    public static final String APPS_VERSION_11 = "11";
    public static final String APPS_VERSION_12 = "12";
    public static final String APPS_VERSION_12_1 = "12.1";
    public static final String APPS_VERSION_12_2 = "12.2";

    public static final String APPS_11_ZIP_FILE = "excel4apps_wands_oracle_r11.zip";
    public static final String APPS_12_ZIP_FILE = "excel4apps_wands_oracle_r12.zip";

    public static final String APPL_TOP_ZIP_FILE = "e4awand_oracle_appl_top.zip";
    public static final String JAVA_TOP_ZIP_FILE = "e4awand_oracle_java_top.zip";

    public static final String APPS_11_SERVLET_FILE = "zone_ias1022.properties";
    public static final String APPS_12_SERVLET_FILE = "orion_web_xml_1013.tmp";

    public static final String APPS_11_MDS = "xxe4a" + File.separator + "11.5.0" + File.separator + "mds";
    public static final String APPS_12_MDS = "xxe4a" + File.separator + "12.0.0" + File.separator + "mds";

    public static final String FND_LOAD_MENU_LDT = "$FND_TOP" + File.separator + "patch" + File.separator + "115"
            + File.separator + "import" + File.separator + "afsload.lct";

    public static final String FND_LOAD_RESP_LDT = "$FND_TOP" + File.separator + "patch" + File.separator + "115"
            + File.separator + "import" + File.separator + "afscursp.lct";

    public static final String FND_LOAD_CP_LDT = "$FND_TOP" + File.separator + "patch" + File.separator + "115"
            + File.separator + "import" + File.separator + "afcpprog.lct";

    public static final String XXE4A_XXE4A_CP_LDT = "XXE4A_XXE4A_CP.ldt";

    public static final String LOG_FILE = "e4awand_gl_oracle_install";

    public static final String KEY_INSTALLER_MODE = "INSTALLER_MODE";
    public static final String KEY_DEBUG = "DB_AND_APPS_TIER";

    public static final String MODE_DB_AND_APPS_TIER = "DB_AND_APPS_TIER";
    public static final String MODE_APPS_TIER = "APPS_TIER";
}
