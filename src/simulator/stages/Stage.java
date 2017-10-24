package simulator.stages;

public enum Stage {

	FETCH(0),
	DECODE(1),
	EXECUTE(2),
	WRITEBACK(3);


	private int _val;

	Stage(int binVal) {
		this._val = binVal;
	}

	public int val() {
		return _val;
	}

	public static Stage fromVal(int opc) {
		for (Stage l : Stage.values()) {
			if (l.val() == opc) return l;
		}

		return null;
	}
}
