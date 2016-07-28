package com.raider.book.custom.sbv;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SlideModel implements SlideContract.Model {
    @Override
    public MappedByteBuffer getMBB(String path) {
        File file = new File(path);
//        maxLen = file.length();
        MappedByteBuffer mbb = null;
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            // TODO expensive
            mbb = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Log.d("test",file.length()+":"+mbb.array().length);
        return mbb;
    }
}
