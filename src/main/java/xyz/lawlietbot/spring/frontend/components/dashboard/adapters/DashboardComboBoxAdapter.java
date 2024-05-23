package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import dashboard.component.DashboardComboBox;
import dashboard.data.DiscordEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.vaadin.gatanaso.MultiselectComboBox;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class DashboardComboBoxAdapter extends FlexLayout {

    public DashboardComboBoxAdapter(long guildId, long userId, DashboardComboBox dashboardComboBox) {
        setFlexDirection(FlexDirection.ROW);
        setAlignItems(Alignment.END);

        if (dashboardComboBox.getMax() > 1) {
            MultiselectComboBox<DiscordEntity> multiselectComboBox = new MultiselectComboBox<>();
            multiselectComboBox.setWidthFull();
            multiselectComboBox.getStyle().set("padding-top", "0");
            multiselectComboBox.setPlaceholder(dashboardComboBox.getPlaceholder() != null ? dashboardComboBox.getPlaceholder() : getTranslation("dash.select." + dashboardComboBox.getDataType().name(), true));
            multiselectComboBox.setLabel(dashboardComboBox.getLabel());
            multiselectComboBox.setItemLabelGenerator(DiscordEntity::getName);
            multiselectComboBox.setRenderer(new ComponentRenderer<>(discordEntity -> new Text(discordEntity.getName())));
            multiselectComboBox.setOrdered(true);
            multiselectComboBox.setAllowCustomValues(dashboardComboBox.getAllowCustomValues());
            multiselectComboBox.setEnabled(dashboardComboBox.isEnabled());
            if (dashboardComboBox.getDataType() == DashboardComboBox.DataType.CUSTOM) {
                multiselectComboBox.setItems(dashboardComboBox.getValues());
            } else {
                multiselectComboBox.setDataProvider(generateDataProvider(guildId, userId, dashboardComboBox));
            }
            if (!dashboardComboBox.getSelectedValues().isEmpty()) {
                multiselectComboBox.setValue(new HashSet<>(dashboardComboBox.getSelectedValues()));
            }
            multiselectComboBox.addValueChangeListener(e -> {
                if (e.getValue().size() > e.getOldValue().size()) {
                    if (e.getValue().size() > dashboardComboBox.getMax()) {
                        multiselectComboBox.setValue(e.getOldValue());
                    } else {
                        ArrayList<DiscordEntity> tempEntityList = new ArrayList<>(e.getValue());
                        tempEntityList.removeAll(e.getOldValue());
                        dashboardComboBox.triggerAdd(tempEntityList.get(0).getId());
                    }
                } else if (e.getValue().size() < e.getOldValue().size()) {
                    ArrayList<DiscordEntity> tempEntityList = new ArrayList<>(e.getOldValue());
                    tempEntityList.removeAll(e.getValue());
                    dashboardComboBox.triggerRemove(tempEntityList.get(0).getId());
                }
            });
            if (dashboardComboBox.getAllowCustomValues()) {
                multiselectComboBox.addCustomValuesSetListener(e -> {
                    if (multiselectComboBox.getSelectedItems().size() < dashboardComboBox.getMax()) {
                        multiselectComboBox.select(new DiscordEntity(e.getDetail(), e.getDetail()));
                    }
                });
            }
            add(multiselectComboBox);
        } else {
            Div iconDiv = new Div();
            iconDiv.setVisible(dashboardComboBox.getDataType() == DashboardComboBox.DataType.EMOJI);
            iconDiv.addClassName("dashboard-emoji-field");
            if (!dashboardComboBox.getSelectedValues().isEmpty() && dashboardComboBox.getSelectedValues().get(0).getIconUrl() != null) {
                iconDiv.add(generateEmojiPreview(dashboardComboBox.getSelectedValues().get(0).getIconUrl()));
            }

            ComboBox<DiscordEntity> comboBox = new ComboBox<>();
            comboBox.setWidthFull();
            comboBox.getStyle().set("margin-top", "-1em");
            comboBox.setPlaceholder(dashboardComboBox.getPlaceholder() != null ? dashboardComboBox.getPlaceholder() : getTranslation("dash.select." + dashboardComboBox.getDataType().name(), false));
            comboBox.setLabel(dashboardComboBox.getLabel());
            comboBox.setClearButtonVisible(dashboardComboBox.getCanBeEmpty());
            comboBox.setItemLabelGenerator(DiscordEntity::getName);
            comboBox.setRenderer(new ComponentRenderer<>(discordEntity -> {
                if (discordEntity.getIconUrl() != null) {
                    Span span = new Span();
                    if (discordEntity.getIconUrl().startsWith("http")) {
                        Image icon = new Image(discordEntity.getIconUrl(), "");
                        icon.getStyle().set("height", "1em")
                                .set("margin-right", "8px");
                        span.add(icon);
                    } else {
                        Span innerSpan = new Span(discordEntity.getIconUrl());
                        innerSpan.getStyle().set("margin-right", "8px");
                        span.add(innerSpan);
                    }
                    span.add(new Text(discordEntity.getName()));
                    return span;
                } else {
                    return new Text(discordEntity.getName());
                }
            }));
            comboBox.setAllowCustomValue(dashboardComboBox.getAllowCustomValues());
            comboBox.setEnabled(dashboardComboBox.isEnabled());
            if (dashboardComboBox.getDataType() == DashboardComboBox.DataType.CUSTOM) {
                comboBox.setItems(dashboardComboBox.getValues());
            } else {
                comboBox.setDataProvider(generateDataProvider(guildId, userId, dashboardComboBox));
            }
            if (!dashboardComboBox.getSelectedValues().isEmpty()) {
                comboBox.setValue(new ArrayList<>(dashboardComboBox.getSelectedValues()).get(0));
            }
            comboBox.addValueChangeListener(e -> {
                iconDiv.removeAll();
                if (e.getValue() != null && e.getValue().getIconUrl() != null) {
                    iconDiv.add(generateEmojiPreview(e.getValue().getIconUrl()));
                }

                if (e.getValue() != null) {
                    dashboardComboBox.triggerSet(e.getValue().getId());
                } else if (dashboardComboBox.getCanBeEmpty()) {
                    dashboardComboBox.triggerSet(null);
                } else {
                    comboBox.setValue(e.getOldValue());
                }
            });

            add(iconDiv, comboBox);
        }

    }

    private DataProvider<DiscordEntity, String> generateDataProvider(long guildId, long userId, DashboardComboBox dashboardComboBox) {
        return DataProvider.fromFilteringCallbacks(query -> {
            JSONObject json = new JSONObject();
            json.put("type", dashboardComboBox.getDataType());
            json.put("user_id", userId);
            json.put("offset", query.getOffset());
            json.put("limit", query.getLimit());
            json.put("filter_text", query.getFilter().orElse(""));
            return SendEvent.sendToGuild(EventOut.DASH_LIST_DISCORD_ENTITIES, json, guildId)
                    .thenApply(r -> {
                        ArrayList<DiscordEntity> discordEntities = new ArrayList<>();
                        JSONArray entitiesJson = r.getJSONArray("entities");
                        for (int i = 0; i < entitiesJson.length(); i++) {
                            JSONObject entityJson = entitiesJson.getJSONObject(i);
                            discordEntities.add(DiscordEntity.fromJson(entityJson));
                        }
                        return Collections.unmodifiableList(discordEntities);
                    }).join().stream();
        }, query -> {
            JSONObject json = new JSONObject();
            json.put("type", dashboardComboBox.getDataType());
            json.put("user_id", userId);
            json.put("filter_text", query.getFilter().orElse(""));
            return SendEvent.sendToGuild(EventOut.DASH_COUNT_DISCORD_ENTITIES, json, guildId)
                    .thenApply(r -> r.getLong("count")).join().intValue();
        });
    }

    private Component generateEmojiPreview(String imageUrl) {
        if (imageUrl.startsWith("http")) {
            Image icon = new Image(imageUrl, "");
            icon.setHeight("2.5rem");
            return icon;
        } else {
            Span span = new Span(imageUrl);
            span.getStyle().set("font-size", "2rem");
            return span;
        }
    }

}
