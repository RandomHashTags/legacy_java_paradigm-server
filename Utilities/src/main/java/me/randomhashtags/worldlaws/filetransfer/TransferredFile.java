package me.randomhashtags.worldlaws.filetransfer;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public final class TransferredFile {

    private final String fileName;
    private final byte[] bytes;

    public TransferredFile(File file) throws Exception {
        fileName = file.getName();

        final FileInputStream input = new FileInputStream(file);
        final FileChannel channel = input.getChannel();
        final MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        buffer.rewind();
        bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        channel.close();
    }
    public TransferredFile(InputStream inputStream) throws Exception {
        final byte[] fileNameSize = inputStream.readNBytes(2);
        final int fileNameLength = Integer.parseInt(new String(fileNameSize));
        fileName = new String(inputStream.readNBytes(fileNameLength));

        final byte[] remainingBytes = inputStream.readAllBytes();
        final String string = new String(remainingBytes);
        final JSONArray array = new JSONArray(string);
        final int length = array.length();
        final byte[] contentBytes = new byte[length];
        for(int i = 0; i < length; i++) {
            contentBytes[i] = (byte) array.getInt(i);
        }
        bytes = contentBytes;
    }

    public String getFileName() {
        return fileName;
    }
    public byte[] getBytes() {
        return bytes;
    }
}
