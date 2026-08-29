package name.modid;

import java.util.HashMap;
import java.util.Map;

public class NoteBlockLayout {

    public String name;

    public Map<Integer, NoteBlockData> noteBlocks =
            new HashMap<>();

    public NoteBlockLayout() {
    }

    public NoteBlockLayout(String name) {
        this.name = name;
    }
}
