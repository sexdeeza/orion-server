package provider.WzXML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataProvider;

@Slf4j
public class XMLWZFile implements MapleDataProvider {

    private final File root;
    private final WZDirectoryEntry rootForNavigation;

    public XMLWZFile(File fileIn) {
        root = fileIn;
        rootForNavigation = new WZDirectoryEntry(fileIn.getName(), 0, 0, null);
        fillMapleDataEntitys(root, rootForNavigation);
    }

    private void fillMapleDataEntitys(File lroot, WZDirectoryEntry wzdir) {
        for (File file : lroot.listFiles()) {
            String fileName = file.getName();

            if (file.isDirectory() && !fileName.endsWith(".img")) {
                WZDirectoryEntry newDir = new WZDirectoryEntry(fileName, 0, 0, wzdir);
                wzdir.addDirectory(newDir);
                fillMapleDataEntitys(file, newDir);

            } else if (fileName.endsWith(".xml")) { // get the real size here?
                wzdir.addFile(new WZFileEntry(fileName.substring(0, fileName.length() - 4), 0, 0, wzdir));
            }
        }
    }

    @Override
    public MapleData getData(String path) {
        File dataFile = new File(root, path + ".xml");
        File imageDataDir = new File(root, path);
        if (!dataFile.exists()) {
            throw new RuntimeException("Datafile " + path + " does not exist in " + root.getAbsolutePath());
        }
        FileInputStream fis;
        try {
            fis = new FileInputStream(dataFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Datafile " + path + " does not exist in " + root.getAbsolutePath());
        }
        final XMLDomMapleData domMapleData;
        try {
            domMapleData = new XMLDomMapleData(fis, imageDataDir.getParentFile());
        } catch (Exception ex) {
            throw ex;
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return domMapleData;
    }

    @Override
    public MapleDataDirectoryEntry getRoot() {
        return rootForNavigation;
    }
}
