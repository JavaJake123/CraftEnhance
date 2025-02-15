package com.dutchjelly.craftenhance.gui.guis;

import com.dutchjelly.craftenhance.files.CategoryData;
import com.dutchjelly.craftenhance.files.MenuSettingsCache;
import com.dutchjelly.craftenhance.gui.guis.editors.RecipesViewerCategorysSettings;
import com.dutchjelly.craftenhance.gui.templates.MenuTemplate;
import com.dutchjelly.craftenhance.gui.util.ButtonType;
import com.dutchjelly.craftenhance.gui.util.FormatListContents;
import com.dutchjelly.craftenhance.gui.util.GuiUtil;
import com.dutchjelly.craftenhance.gui.util.InfoItemPlaceHolders;
import com.dutchjelly.craftenhance.messaging.Messenger;
import com.dutchjelly.craftenhance.prompt.HandleChatInput;
import com.dutchjelly.craftenhance.util.PermissionTypes;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.dutchjelly.craftenhance.CraftEnhance.self;
import static com.dutchjelly.craftenhance.gui.util.GuiUtil.setTextItem;

public class RecipesViewerCategorys extends MenuHolder {
	private final MenuSettingsCache menuSettingsCache = self().getMenuSettingsCache();
	private final MenuTemplate menuTemplate;

	public RecipesViewerCategorys(final String grupSeachFor) {
		super(FormatListContents.getCategorys(self().getCategoryDataCache().values(), grupSeachFor));
		this.menuTemplate = menuSettingsCache.getTemplates().get("RecipesCategorys");
		setFillSpace(this.menuTemplate.getFillSlots());
		setTitle(this.menuTemplate.getMenuTitel());
		setMenuSize(GuiUtil.invSize("RecipesCategorys",this.menuTemplate.getAmountOfButtons()));
		setMenuOpenSound(this.menuTemplate.getSound());
		this.setUseColorConversion(true);
	}

	@Override
	public MenuButton getFillButtonAt(@Nonnull final Object object) {
		return new MenuButton() {
			@Override
			public void onClickInsideMenu(@Nonnull final Player player, @Nonnull final Inventory inventory, @Nonnull final ClickType clickType, @Nonnull final ItemStack itemStack, final Object o) {
				if (o instanceof CategoryData) {
					if (clickType == ClickType.LEFT)
						new RecipesViewer((CategoryData) o, "", player).menuOpen(player);
					else if (player.hasPermission(PermissionTypes.Categorys_editor.getPerm())){
						new RecipesViewerCategorysSettings(((CategoryData) o).getRecipeCategory()).menuOpen(player);
					}
				}
			}

			@Override
			public ItemStack getItem(@Nonnull final Object object) {
				if (object instanceof CategoryData) {
					String displayName = " ";
					List<String> lore = new ArrayList<>();
					final Map<String, String> placeHolders = new HashMap<>();
					if (menuTemplate != null) {
						final com.dutchjelly.craftenhance.gui.templates.MenuButton menuButton = menuTemplate.getMenuButton(-1);
						if (menuButton != null) {
							displayName = menuButton.getDisplayName();
							lore = menuButton.getLore();
						}
					}
					final ItemStack itemStack = ((CategoryData) object).getRecipeCategoryItem();
					setTextItem(itemStack, displayName, lore);
					String categoryName = ((CategoryData) object).getDisplayName();
					if (categoryName == null || categoryName.equals(""))
						categoryName = ((CategoryData) object).getRecipeCategory();
					placeHolders.put(InfoItemPlaceHolders.DisplayName.getPlaceHolder(), categoryName);
					return GuiUtil.ReplaceAllPlaceHolders(itemStack.clone(), placeHolders);
				}
				return null;
			}

			@Override
			public ItemStack getItem() {
				return null;
			}
		};
	}
	@Override
	public MenuButton getButtonAt(final int slot) {
		if (this.menuTemplate == null) return null;
		for (final Entry<List<Integer>, com.dutchjelly.craftenhance.gui.templates.MenuButton> menuTemplate : this.menuTemplate.getMenuButtons().entrySet()){
			if (menuTemplate.getKey().contains(slot)){
				return registerButtons(menuTemplate.getValue());
			}
		}
		return null;
	}


	private MenuButton registerButtons(final com.dutchjelly.craftenhance.gui.templates.MenuButton value) {
		return new MenuButton() {
			@Override
			public void onClickInsideMenu(@Nonnull final Player player, @Nonnull final Inventory menu, @Nonnull final ClickType click, @Nonnull final ItemStack clickedItem, final Object object) {
				if (run(value, menu, player, click))
					updateButtons();
			}

			@Override
			public ItemStack getItem() {
				return value.getItemStack();
			}
		};
	}
	public boolean run(final com.dutchjelly.craftenhance.gui.templates.MenuButton value, final Inventory menu, final Player player, final ClickType click) {
		if (value.getButtonType() == ButtonType.PrvPage){
			previousPage();
			return true;
		}
		if (value.getButtonType() == ButtonType.NxtPage){
			nextPage();
			return true;
		}
		if (value.getButtonType() == ButtonType.Search) {
			if (click == ClickType.RIGHT) {
				Messenger.Message("Search for categorys.", getViewer());
				new HandleChatInput(this, msg-> {
					if (GuiUtil.seachCategory(msg)){
						new RecipesViewerCategorys( msg).menuOpen(getViewer());
						return false;
					}
					return true;
				}).setMessages("Search for categorys.")
						.start(getViewer());;
			}
			else new RecipesViewerCategorys("").menuOpen(player);
		}
		if (value.getButtonType() == ButtonType.NewCategory && player.hasPermission(PermissionTypes.Categorys_editor.getPerm())){
			new HandleChatInput(this, msg-> {
				if (!GuiUtil.newCategory(msg, player)) {
					new RecipesViewerCategorys("").menuOpen(player);
					return false;
				}
				return true;
			}).setMessages("Please input your category name and item type you want. Like this 'category' without '.Type cancel, quit, exit or q to close this without change.")
					.start(getViewer());
	/*		self().getGuiManager().waitForChatInput(new RecipesViewerCategorys(""), getViewer(), msg-> {
				if (!GuiUtil.newCategory(msg, player)) {
					new RecipesViewerCategorys("").menuOpen(player);
					return false;
				}
				return true;
			});*/
		}
		return false;
	}
}
