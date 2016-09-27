package interQA.main;

import static interQA.cache.JenaExecutorCacheSelect.interactiveExplorerForCacheinDiskSpecificFile;

/**
 * Created by Mariano on 26/09/2016.
 */

public class SelectCacheBrowser {

    static public void main (String[] args) {
        if (args.length != 1){
            System.out.println("Missing argument (.ser filename with full path)");
            System.exit(1);
        }else{
            String fileName = args[0];
            interactiveExplorerForCacheinDiskSpecificFile(fileName);
        }
    }
}