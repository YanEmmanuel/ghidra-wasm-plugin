package wasm.format.sections.structures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ghidra.app.util.bin.BinaryReader;
import ghidra.app.util.bin.StructConverter;
import ghidra.program.model.data.DataType;
import ghidra.program.model.data.Structure;
import ghidra.util.exception.DuplicateNameException;
import wasm.format.Leb128;
import wasm.format.StructureUtils;

public class WasmNameMap implements StructConverter {
	private Leb128 count;
	private List<WasmAssoc> entries = new ArrayList<>();
	private Map<Long, WasmName> map = new HashMap<>();

	private static class WasmAssoc {
		Leb128 idx;
		WasmName name;
	}

	public WasmNameMap(BinaryReader reader) throws IOException {
		count = new Leb128(reader);
		for (int i = 0; i < count.getValue(); i++) {
			WasmAssoc assoc = new WasmAssoc();
			assoc.idx = new Leb128(reader);
			assoc.name = new WasmName(reader);
			entries.add(assoc);
			map.put(assoc.idx.getValue(), assoc.name);
		}
	}

	public String getEntry(long idx) {
		WasmName result = map.get(idx);
		if (result == null)
			return null;
		return result.getValue();
	}

	@Override
	public DataType toDataType() throws DuplicateNameException, IOException {
		Structure structure = StructureUtils.createStructure("namemap");
		StructureUtils.addField(structure, count, "count");
		for (int i = 0; i < entries.size(); i++) {
			WasmAssoc assoc = entries.get(i);
			StructureUtils.addField(structure, assoc.idx, "idx" + i);
			StructureUtils.addField(structure, assoc.name, "name" + i);
		}
		return structure;
	}
}
