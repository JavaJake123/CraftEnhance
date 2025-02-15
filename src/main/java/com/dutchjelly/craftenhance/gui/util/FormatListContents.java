package com.dutchjelly.craftenhance.gui.util;

import com.dutchjelly.craftenhance.crafthandling.recipes.EnhancedRecipe;
import com.dutchjelly.craftenhance.files.CategoryData;
import com.dutchjelly.craftenhance.gui.guis.editors.IngredientsCache;
import com.dutchjelly.craftenhance.util.PermissionTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dutchjelly.craftenhance.CraftEnhance.self;

public class FormatListContents {

	public static <RecipeT extends EnhancedRecipe> List<?> formatRecipes(final RecipeT recipe, final IngredientsCache ingredientsCache, final boolean loadCachedItems) {
		if (recipe == null) return new ArrayList<>();
		List<ItemStack> list = new ArrayList<>(Arrays.asList(recipe.getContentItems()));
		ItemStack result = recipe.getResult().getItem();
		if (ingredientsCache != null && loadCachedItems){
			final ItemStack itemStackResult = ingredientsCache.getItemStackResult();
			final ItemStack[] ingredients = ingredientsCache.getItemStacks();
			if (itemStackResult != null || ingredients != null){
				list = new ArrayList<>(Arrays.asList(ingredients != null ? ingredients: new ItemStack[0]));
				result = itemStackResult;
			}
		}
		//todo fix so it auto set right craftingslot
		final int index;
		if (list.size() < 6)
			index = 1;
		else
			index = 6;
		list.add(index, result);
		return list;
	}

	public static List<EnhancedRecipe> canSeeRecipes(final List<EnhancedRecipe> enhancedRecipes, final Player p) {
		return enhancedRecipes.stream().filter(x -> (
				!self().getConfig().getBoolean("only-show-available") ||
						x.getPermissions() == null ||
						Objects.equals(x.getPermissions(), "") ||
						p.hasPermission(x.getPermissions()))
				&& (!x.isHidden() ||
				p.hasPermission(PermissionTypes.Edit.getPerm()) ||
				p.hasPermission(x.getPermissions() + ".hidden"))).collect(Collectors.toList());
	}

	public static List<CategoryData> getCategorys(final Collection<CategoryData> categoryData, final String grupSeachFor) {
		if (grupSeachFor == null || grupSeachFor.equals(""))
			return new ArrayList<>(categoryData);
		return categoryData.stream().filter(x -> x.getRecipeCategory().contains(grupSeachFor)).collect(Collectors.toList());
	}
	public static List<String> getCategorys(final Set<String> categoryNames, final String grupSeachFor) {
		if (grupSeachFor == null || grupSeachFor.equals(""))
			return new ArrayList<>(categoryNames);
		return categoryNames.stream().filter(x -> x.contains(grupSeachFor)).collect(Collectors.toList());
	}
	public static List<Recipe> getRecipes(final List<Recipe> enabledRecipes, final List<Recipe> disabledRecipes, final boolean enableMode, final String grupSeachFor) {
		final List<Recipe> recipes = !enableMode ? enabledRecipes : disabledRecipes;
		if (grupSeachFor == null || grupSeachFor.equals("")) {
			return recipes;
		}
		return recipes.stream().filter( recipe -> recipe.getResult().getType().name().contains(grupSeachFor.toUpperCase())).collect(Collectors.toList());
	}
}
