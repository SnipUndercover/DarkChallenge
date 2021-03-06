package ovh.snipundercover.darkchallenge.permission;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;
import java.util.Map;

@ToString
@EqualsAndHashCode
public class PermissionNode {
	
	//god the annotations
	
	@Getter
	private final PermissionNode parent;
	
	@Getter
	@NotNull
	private final String name;
	
	@Getter
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private final String permissionString;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private final Map<String, PermissionNode> subPermissionLookup = new Hashtable<>();
	
	public PermissionNode(@NotNull String name) {
		this(null, name);
	}
	
	private PermissionNode(PermissionNode parent, @NotNull String name) {
		if (name.isBlank())
			throw new IllegalArgumentException(
					"Permission name is empty! [parent: %s]".formatted(parent.permissionString)
			);
		this.parent = parent;
		this.name = name;
		this.permissionString = parent == null
				? name
				: "%s.%s".formatted(parent.permissionString, name);
	}
	
	public boolean hasPermission(@NotNull Permissible permissible) {
		return permissible.hasPermission(getPermissionString());
	}
	
	@Contract(pure = true, value = "_ -> new")
	public PermissionNode getSubPermission(@NotNull String subPermission) {
		if (subPermissionLookup.containsKey(subPermission))
			return subPermissionLookup.get(subPermission);
		PermissionNode newNode = new PermissionNode(this, subPermission);
		subPermissionLookup.put(subPermission, newNode);
		return newNode;
	}
}
