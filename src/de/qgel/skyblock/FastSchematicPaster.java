package de.qgel.skyblock;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;

import net.minecraft.server.Chunk;

public class FastSchematicPaster {

    public void pasteSchematic(World bukkitWorld, File file, int ox, int oy, int oz)
            throws IOException, DataException 
    {
        CuboidClipboard clipboard = CuboidClipboard.loadSchematic(file);

        int width  = clipboard.getWidth();
        int height = clipboard.getHeight();
        int length = clipboard.getLength();

        // ====== Access private byte[] block arrays ======
        byte[] blocks = getPrivateByteArray(clipboard, "blocks");
        byte[] data   = getPrivateByteArray(clipboard, "data");

        net.minecraft.server.World nmsWorld = ((CraftWorld) bukkitWorld).getHandle();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < length; z++) {

                    int index = (y * width + x) * length + z;

                    int id = blocks[index] & 0xFF;
                    int meta = data[index] & 0xFF;

                    if (id == 0) continue;

                    int bx = ox + x;
                    int by = oy + y;
                    int bz = oz + z;

                    Chunk chunk = nmsWorld.getChunkAt(bx >> 4, bz >> 4);

                    chunk.a(bx & 15, by, bz & 15, id, meta);
                }
            }
        }
    }

    private byte[] getPrivateByteArray(CuboidClipboard clipboard, String fieldName) {
        try {
            Field f = CuboidClipboard.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            return (byte[]) f.get(clipboard);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read private field: " + fieldName, e);
        }
    }
}

