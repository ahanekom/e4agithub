package com.excel4apps.servlet.wand.oracle.inst.files;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import com.excel4apps.servlet.wand.oracle.inst.Installer;
import com.excel4apps.servlet.wand.oracle.inst.exceptions.UpdateAdopSyncFileException;
import com.excel4apps.servlet.wand.oracle.inst.utils.FileManager;

public class UpdateAdopSyncFile extends Installer
{

    private static final String ADOP_SYNC_DRV_FILE_TARGET_STRING = "#Copy Starts";
    private static final String ADOP_SYNC_DRV_FILE = "adop_sync.drv";

    public static void update() throws UpdateAdopSyncFileException
    {
        String adopCustomSyncFileString = ic.getOac().getApplTopNe() + File.separator + "ad" + File.separator
                + "custom" + File.separator + ADOP_SYNC_DRV_FILE;

        File adopCustomSyncFile = new File(adopCustomSyncFileString);

        String replacementString = new String();

        String eol = System.getProperty("line.separator");

        String sl1 = ADOP_SYNC_DRV_FILE_TARGET_STRING + eol;
        String sl2 = "mkdir -p %s_other_base%/EBSapps/comn/java/classes/com/excel4apps" + eol;
        String sl3 = "rsync -zr %s_current_base%/EBSapps/comn/java/classes/com/excel4apps %s_other_base%/EBSapps/comn/java/classes/com/excel4apps"
                + eol;

        replacementString = sl1 + sl2 + sl3;

        try
        {
            FileManager.modifyFile(adopCustomSyncFile, ADOP_SYNC_DRV_FILE_TARGET_STRING, replacementString);
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new UpdateAdopSyncFileException("Unable to update Adop Custom Sync File");
        }

    }
}
