import java.util.ArrayList;
import java.util.List;

public class Tier {
	private String name;
	private List<String> wandCommands;

	public Tier(String name) {
		this.name = name;
		wandCommands = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public List<String> getCommands() {
		return wandCommands;
	}

	public void setName(String newName) {
		name = newName;
	}
}
