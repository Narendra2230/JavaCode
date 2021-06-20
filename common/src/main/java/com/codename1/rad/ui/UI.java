/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;


import ca.weblite.shared.components.table.DefaultTableCellEditor;
import ca.weblite.shared.components.table.DefaultTableCellRenderer;
import com.codename1.rad.attributes.*;
import com.codename1.rad.controllers.ViewController;
import com.codename1.rad.events.EventFactory;
import com.codename1.rad.models.*;
import com.codename1.rad.nodes.FormNode;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.nodes.PropertyNode;
import com.codename1.rad.nodes.SectionNode;

import com.codename1.rad.ui.entityviews.EntityListView;
import com.codename1.rad.ui.image.EntityImageRenderer;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ActionsNode;
import com.codename1.rad.nodes.EntityViewFactoryNode;
import com.codename1.rad.nodes.TableColumns;
import com.codename1.rad.nodes.EventFactoryNode;
import com.codename1.rad.nodes.ListNode;
import com.codename1.rad.nodes.PropertyViewFactoryNode;
import com.codename1.rad.nodes.RowTemplateNode;
import com.codename1.rad.nodes.ViewNode;
import ca.weblite.shared.components.table.TableCellEditor;
import ca.weblite.shared.components.table.TableCellRenderer;
import com.codename1.io.File;
import com.codename1.rad.events.DefaultEventFactory;
import com.codename1.rad.models.Property.Editable;
import com.codename1.rad.models.Property.Getter;
import com.codename1.rad.models.Property.GetterAttribute;
import com.codename1.rad.models.Property.Label;
import com.codename1.rad.models.Property.Setter;
import com.codename1.rad.models.Property.SetterAttribute;
import com.codename1.rad.nodes.ActionNode.Category;
import com.codename1.rad.nodes.ActionNode.EnabledCondition;
import com.codename1.rad.nodes.ActionViewFactoryNode;
import com.codename1.rad.nodes.OptionsNode;
import com.codename1.rad.propertyviews.ButtonListPropertyView;
import com.codename1.rad.propertyviews.ButtonListPropertyView.ButtonListLayout;
import com.codename1.rad.text.CurrencyFormatter;
import com.codename1.rad.text.DateFormatter;
import com.codename1.rad.text.DecimalNumberFormatter;
import com.codename1.rad.text.DefaultTextFormatter;
import com.codename1.rad.text.IntegerFormatter;
import com.codename1.rad.text.LocalDateLongStyleFormatter;
import com.codename1.rad.text.LocalDateShortStyleFormatter;
import com.codename1.rad.text.LocalDateTimeFormatter;
import com.codename1.rad.text.LocalDateTimeMediumStyleFormatter;
import com.codename1.rad.text.LocalDateTimeShortStyleFormatter;
import com.codename1.rad.text.NumberFormatter;
import com.codename1.rad.text.TextFormatter;
import com.codename1.rad.text.TimeAgoDateFormatter;
import com.codename1.rad.ui.image.PropertyImageRenderer;
import com.codename1.ui.*;
import com.codename1.ui.list.ListModel;
import com.codename1.util.EasyThread;


/**
 * The base class for UI descriptors. This class sits at the foundation of CodeRAD's dynamic form generation
 * capability.
 * 
 * See {@link FormNode} for some usage examples.
 * 
 * @author shannah
 */
public class UI extends EntityType implements ActionCategories, WidgetTypes {
    private FormNode root;
    private static EntityViewFactory defaultEntityViewFactory;
    private static ActionViewFactory defaultActionViewFactory;
    private static EventFactory defaultEventFactory;
    private static PropertyViewFactory defaultPropertyViewFactory;
    private static TableCellRenderer defaultTableCellRenderer;
    private static TableCellEditor defaultTableCellEditor;
    private static EntityListCellRenderer defaultListCellRenderer;
    private static File tmpDir;
    private static final String CIRCLE_MASK_CACHE_KEY = "circle-mask-";
    
    private static StrongCache cache;
    
    public static StrongCache getCache() {
        if (cache == null) {
            cache = new StrongCache();
        }
        return cache;
    }
    
    public static void setDefaultListCellRenderer(EntityListCellRenderer renderer) {
        defaultListCellRenderer = renderer;
    }
    
    public static EntityListCellRenderer getDefaultListCellRenderer() {
        if (defaultListCellRenderer == null) {
            defaultListCellRenderer = new DefaultEntityListCellRenderer();
        }
        return defaultListCellRenderer;
    }
    
    public static void setDefaultEntityViewFactory(EntityViewFactory factory) {
        defaultEntityViewFactory = factory;
    }


    /**
     * Adds a {@link #LIST_REMOVE_ACTION} used by {@link com.codename1.rad.ui.entityviews.EntityListView}.
     * @param removeAction The action to be used for removes.
     * @return
     */
    public static ActionsNode removeAction(ActionNode removeAction) {
        return actions(LIST_REMOVE_ACTION, removeAction);
    }


    /**
     * Adds a {@link #LIST_SELECT_ACTION} used by {@link com.codename1.rad.ui.entityviews.EntityListView}
     * @param selectAction The action to be used for row selection.
     * @return
     */
    public static ActionsNode selectAction(ActionNode selectAction) {
        return actions(LIST_SELECT_ACTION, selectAction);
    }

    /**
     * Adds a {@link #LIST_ADD_ACTION} used by {@link com.codename1.rad.ui.entityviews.EntityListView}
     * @param insertAction
     * @return
     */
    public static ActionsNode addAction(ActionNode insertAction) {
        return actions(LIST_ADD_ACTION, insertAction);
    }

    /**
     * Adds a {@link #LIST_REFRESH_ACTION} used by {@link com.codename1.rad.ui.entityviews.EntityListView}
     * @param refreshAction The action to be triggered when the list is refreshed.
     * @return
     */
    public static ActionsNode refreshAction(ActionNode refreshAction) {
        return actions(LIST_REFRESH_ACTION, refreshAction);
    }

    /**
     * Adds a {@link #LIST_LOAD_MORE_ACTION} used by {@link com.codename1.rad.ui.entityviews.EntityListView}
     * @param loadMoreAction The action to be triggered when a request is made to load more records in the list.
     * @return
     */
    public static ActionsNode loadMoreAction(ActionNode loadMoreAction) {
        return actions(LIST_LOAD_MORE_ACTION, loadMoreAction);
    }

    /**
     * Specifies the {@link EntityListProvider} to be used by {@link EntityListView}.
     * @param provider
     * @return
     * @see #providerLookup(Class)
     */
    public static EntityListProviderAttribute provider(EntityListProvider provider) {
        return new EntityListProviderAttribute(provider);
    }

    /**
     * Specifies a lookup marker class that should be used to lookup the provider for
     * an EntityListView.  This allows the list view to search up the Controller hierarchy
     * for a provider rather than have a provider explicitly supplied.
     * @param lookupClass The class to use to lookup the provider.  This doesn't have to be a provider class,
     *                    but it is assumed that the object that this "looks up" will be of type {@link EntityListProvider}.
     * @return
     * @see #provider(EntityListProvider) 
     */
    public static EntityListProviderLookup providerLookup(Class lookupClass) {
        return new EntityListProviderLookup(lookupClass);
    }
    
    public static EntityViewFactory getDefaultEntityViewFactory() {
        if (defaultEntityViewFactory == null) {
            defaultEntityViewFactory = new DefaultEntityViewFactory();
        }
        return defaultEntityViewFactory;
    }
    
    public static ActionViewFactory getDefaultActionViewFactory() {
        if (defaultActionViewFactory == null) {
            defaultActionViewFactory = new DefaultActionViewFactory();
        }
        return defaultActionViewFactory;
    }
    
    public static void setDefaultActionViewFactory(ActionViewFactory factory) {
        defaultActionViewFactory = factory;
    }
    
    public static EventFactory getDefaultEventFactory() {
        if (defaultEventFactory == null) {
            defaultEventFactory = new DefaultEventFactory();
        }
        return defaultEventFactory;
    }
    
    public static void setDefaultEventFactory(EventFactory factory) {
        defaultEventFactory = factory;
    }
    
    public static PropertyViewFactory getDefaultPropertyViewFactory() {
        if (defaultPropertyViewFactory == null) {
            defaultPropertyViewFactory = new DefaultPropertyViewFactory();
        }
        return defaultPropertyViewFactory;
    }
    
    public static void setDefaultPropertyViewFactory(PropertyViewFactory factory) {
        defaultPropertyViewFactory = factory;
    }
    
    public static TableCellRenderer getDefaultTableCellRenderer() {
        if (defaultTableCellRenderer == null) {
            defaultTableCellRenderer = new DefaultTableCellRenderer();
        }
        return defaultTableCellRenderer;
    }
    
    public static void setDefaultTableCellRenderer(TableCellRenderer renderer) {
        defaultTableCellRenderer = renderer;
    }
    
    public static TableCellEditor getDefaultTableCellEditor() {
        if (defaultTableCellEditor == null) {
            defaultTableCellEditor = new DefaultTableCellEditor();
        }
        return defaultTableCellEditor;
    }
    
    public static void setDefaultTableCellEditor(TableCellEditor editor) {
        defaultTableCellEditor = editor;
    }
    
    public static File getTempDir() {
        if (tmpDir == null) {
            tmpDir = new File("CodeRadTmp");
            tmpDir.mkdirs();
        }
        return tmpDir;
    }
    
    public static File getTempFile(String name) {
        if (name.startsWith("file:/")) {
            return new File(name);
        } else {
            return new File(getTempDir(), name);
        }
    }
    
    
    public static EntityViewFactoryNode viewFactory(EntityViewFactory factory, Attribute... atts) {
        return new EntityViewFactoryNode(factory, atts);
    }
    
    public static ActionViewFactoryNode actionViewFactory(ActionViewFactory factory, Attribute... atts) {
        return new ActionViewFactoryNode(factory, atts);
    }
    
    public static ActionViewFactoryNode viewFactory(ActionViewFactory factory, Attribute... atts) {
        return actionViewFactory(factory, atts);
    }
    

    private static EasyThread imageProcessingThread;
    protected FormNode form(Attribute... atts) {
        root = new FormNode(atts);
        return root;
    }
    
    public static UIID uiid(String uiid) {
        return new UIID(uiid);
    }
    
    public static UIID uiid(StringProvider provider) {
        return new UIID("", provider);
    }
    
    public static UIID uiid(String uiid, StringProvider provider) {
        return new UIID(uiid, provider);
    }
    
    public static IconUIID iconUiid(String uiid) {
        return new IconUIID(uiid);
    }
    
    public static BadgeUIID badgeUiid(String uiid) {
        return new BadgeUIID(uiid);
    }
    
    public static Badge badge(String badgeText) {
        return new Badge(badgeText);
    }
    
    public static Badge badge(String badgeText, StringProvider provider) {
        return new Badge(badgeText, provider);
    }
    
    public static SectionNode section(Attribute... atts) {
        return new SectionNode(atts);
    }
    
    public static FieldNode field(Attribute... atts) {
        return new FieldNode(atts);
    }
    
    public static Columns columns(int cols) {
        return new Columns(cols);
    }
    
    public static PropertyNode property(Property prop, Attribute... atts) {
        return new PropertyNode(prop, atts);
    }
    
    public static PropertySelectorAttribute property(PropertySelectorProvider value) {
        return new PropertySelectorAttribute(value);
    }
    
    public static PropertySelectorAttribute property(PropertySelector value) {
        return property((PropertySelectorProvider)(entity -> {
            return value;
        }));
    }
    
    public static PropertyImageRendererAttribute iconRenderer(PropertyImageRenderer renderer) {
        return new PropertyImageRendererAttribute(renderer);
    }
    
    public static ActionStyleAttribute actionStyle(ActionStyle style) {
        return new ActionStyleAttribute(style);
    }
    
    public static NodeDecoratorAttribute decorator(NodeDecorator decorator) {
        return new NodeDecoratorAttribute(decorator);
    }
    
    public static FieldNode textField(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(TEXT);
        return fieldNode;
    }
    
    public static FieldNode htmlComponent(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(HTML_COMPONENT);
        return fieldNode;
    }
    
    public static ButtonListPropertyView.ButtonListLayoutAttribute buttonListLayout(ButtonListPropertyView.ButtonListLayout layout) {
        return new ButtonListPropertyView.ButtonListLayoutAttribute(layout);
    }
    
    public static ButtonListPropertyView.ButtonListLayoutAttribute buttonListY() {
        return buttonListLayout(ButtonListLayout.Y);
    }
    
    public static ButtonListPropertyView.ButtonListLayoutAttribute buttonListX() {
        return buttonListLayout(ButtonListLayout.X);
    }
    
    public static ButtonListPropertyView.ButtonListLayoutAttribute buttonListFlow() {
        return buttonListLayout(ButtonListLayout.Flow);
    }
    
    public static ButtonListPropertyView.ButtonListLayoutAttribute buttonListGrid() {
        return buttonListLayout(ButtonListLayout.Grid);
    }
    
    
    public static FieldNode radioList(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(RADIO_LIST);
        return fieldNode;
    }
    
    public static FieldNode radioListY(Attribute... atts) {
        FieldNode out = radioList(atts);
        out.setAttributes(buttonListY());
        return out;
    }
    
    public static FieldNode checkboxList(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(CHECKBOX_LIST);
        return fieldNode;
    }
    
    public static FieldNode checkboxListY(Attribute... atts) {
        FieldNode fieldNode = checkboxList(atts);
        fieldNode.setAttributes(buttonListY());
        return fieldNode;
    }
    
    public static FieldNode switchList(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(SWITCH_LIST);
        return fieldNode;
    }
    
    public static FieldNode checkbox(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(CHECKBOX);
        return fieldNode;
    }
    
    public static FieldNode radio(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(RADIO);
        return fieldNode;
    }
    
    public static FieldNode toggleSwitch(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(SWITCH);
        return fieldNode;
    }
    
    public static FieldNode switchListY(Attribute... atts) {
        FieldNode fieldNode = switchList(atts);
        fieldNode.setAttributes(buttonListY());
        return fieldNode;
    }
    
    public static FieldNode textArea(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(TEXTAREA);
        return fieldNode;
    }
    
    public static FieldNode comboBox(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(COMBOBOX);
        return fieldNode;
    }
    
    public static TableColumns columns(FieldNode... atts) {
        TableColumns columnsNode = new TableColumns(atts);
        return columnsNode;
        
    }
    
    public static FieldNode table(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(TABLE);
        return fieldNode;
    }
    
    public static TableCellRendererAttribute cellRenderer(TableCellRenderer renderer) {
        return new TableCellRendererAttribute(renderer);
    }
    
    public static TableCellEditorAttribute cellEditor(TableCellEditor editor) {
        return new TableCellEditorAttribute(editor);
    }
    
    public static ListCellRendererAttribute cellRenderer(EntityListCellRenderer renderer) {
        return new ListCellRendererAttribute(renderer);
    }
    
    public static  PropertyViewFactoryNode propertyViewFactory(PropertyViewFactory factory, Attribute... atts) {
        return new PropertyViewFactoryNode(factory, atts);
    }
    
    public static  PropertyViewFactoryNode viewFactoryFactory(PropertyViewFactory factory, Attribute... atts) {
        return new PropertyViewFactoryNode(factory, atts);
    }
    
    public static PropertyViewFactoryNode factory(PropertyViewFactory factory, Attribute... atts) {
        return propertyViewFactory(factory, atts);
    }
    
    public static PropertyViewFactoryNode propertyView(PropertyViewFactory factory, Attribute... atts) {
        return propertyViewFactory(factory, atts);
    }
    
    public static EventFactoryNode eventFactory(EventFactory factory, Attribute... atts) {
        return new EventFactoryNode(factory, atts);
    }
    
    public static EventFactoryNode event(EventFactory factory, Attribute... atts) {
        return eventFactory(factory, atts);
    }
    
    public static EventFactoryNode factory(EventFactory factory, Attribute... atts) {
        return eventFactory(factory, atts);
    }
    
    public static ActionNode action(Attribute... atts) {
        return new ActionNode(atts);
    }
    
    public static SelectedCondition selectedCondition(EntityTest test) {
        return new SelectedCondition(test);
    }
    
    public static EnabledCondition enabledCondition(EntityTest test) {
        return new EnabledCondition(test);
    }
    
    public static Condition condition(EntityTest test) {
        return new Condition(test);
    }
    
    public static ActionNode.Selected selected(Attribute... atts) {
        return new ActionNode.Selected(atts);
    }
    
    public static ActionNode.Disabled disabled(Attribute... atts) {
        return new ActionNode.Disabled(atts);
    }
    
    public static ActionNode.Pressed pressed(Attribute... atts) {
        return new ActionNode.Pressed(atts);
    }
    
    public static ActionsNode actions(Attribute...atts) {
        return new ActionsNode(atts);
    }
    
    public static ActionsNode actions(Category category, Actions actions) {
        ActionsNode out = actions(actions.toArray());
        out.setAttributes(category);
        return out;
    }
    
    public static MaterialIcon icon(char icon) {
        return new MaterialIcon(icon);
    }
    
    public static ImageIcon icon(Image icon) {
        return new ImageIcon(icon);
    }
    
    public static TextFormatterAttribute textFormat(TextFormatter fmt) {
        return new TextFormatterAttribute(fmt);
    }
    
    public static TextFormatterAttribute textFormat(DefaultTextFormatter.FormatCallback fmt) {
        return new TextFormatterAttribute(new DefaultTextFormatter(fmt));
    }
    
    public static TextFormatterAttribute textFormat(DefaultTextFormatter.FormatCallback fmt, DefaultTextFormatter.ParseCallback parse) {
        return new TextFormatterAttribute(new DefaultTextFormatter(fmt, parse));
    }
    
    public static DateFormatterAttribute dateFormat(DateFormatter fmt) {
        return new DateFormatterAttribute(fmt);
    }
    
    public static DateFormatterAttribute shortDateFormat() {
        return dateFormat(new LocalDateShortStyleFormatter());
    }
    
    public static DateFormatterAttribute longDateFormat() {
        return dateFormat(new LocalDateLongStyleFormatter());
    }
    
    public static DateFormatterAttribute dateTimeFormat() {
        return dateFormat(new LocalDateTimeFormatter());
    }
    
    public static DateFormatterAttribute shortDateTimeFormat() {
        return dateFormat(new LocalDateTimeShortStyleFormatter());
    }
    
    public static DateFormatterAttribute mediumDateTimeFormat() {
        return dateFormat(new LocalDateTimeMediumStyleFormatter());
    }
    
    public static DateFormatterAttribute timeAgoFormat() {
        return dateFormat(new TimeAgoDateFormatter());
    }
    
    public static NumberFormatterAttribute decimalFormat(int decimalPlaces) {
        return new NumberFormatterAttribute(new DecimalNumberFormatter(decimalPlaces));
    }
    
    public static NumberFormatterAttribute currencyFormat() {
        return new NumberFormatterAttribute(new CurrencyFormatter());
    }
    
    public static NumberFormatterAttribute intFormat() {
        return new NumberFormatterAttribute(new IntegerFormatter());
    }
    
    public static Editable editable(boolean editable) {
        return new Editable(editable);
    }
    
    
    public FormNode getRoot() {
        return root;
    }
    
    public FieldNode[] getAllFields() {
        return getRoot().getAllFields();
    }
    
    
    
    public static RowTemplateNode rowTemplate(Attribute... atts) {
        ViewNode node = new ViewNode(atts);
        return new RowTemplateNode(node);
    }
    
    public static ListNode list(Attribute... atts) {
        return new ListNode(atts);
    }
    
    public static IconRendererAttribute iconRenderer(EntityImageRenderer renderer) {
        return new IconRendererAttribute(renderer);
    }

    public static ViewControllerAttribute controller(ViewController ctl) {
        return new ViewControllerAttribute(ctl);
    }
    
    public static ViewNode view(Attribute... atts) {
        return new ViewNode(atts);
    }
    
    public static <T> ViewPropertyParameterAttribute<T> param(ViewProperty<T> prop, T value) {
        return new ViewPropertyParameterAttribute<T>(ViewPropertyParameter.createValueParam(prop, value));
    }
    
    public static <T> ViewPropertyParameterAttribute<T> param(ViewProperty<T> prop, Tag... tags) {
        return new ViewPropertyParameterAttribute<T>(ViewPropertyParameter.createBindingParam(prop, tags));
    }
    
    public static TextIcon icon(String text) {
        return new TextIcon(text);
    }
    
    public static TextIcon icon(String text, StringProvider provider) {
        return new TextIcon(text, provider);
    }
    
    public static TextIcon icon(StringProvider provider) {
        return new TextIcon("", provider);
    }
            
    
    public static Label label(String label) {
        return new Label(label);
    }
    
    public static Label label(String label, StringProvider provider) {
        return new Label(label, provider);
    }
    
    public static Label label(StringProvider provider) {
        return new Label("", provider);
    }
    
    public static OptionsNode options(ListModel model, Attribute... atts) {
        return new OptionsNode(model, atts);
    }
    
    public synchronized static void runOnImageProcessingThread(Runnable r) {
        if (imageProcessingThread == null) {
            imageProcessingThread = EasyThread.start("ImageProcessingThread");
            
        }
        imageProcessingThread.run(r);
    }
    
    public static GetterAttribute getter(Getter g) {
        return new GetterAttribute(g);
    }
    
    public static SetterAttribute setter(Setter s) {
        return new SetterAttribute(s);
    }

    public static Object getCircleMask(int size) {
        String cacheKey = CIRCLE_MASK_CACHE_KEY+size;
        Object mask = UI.getCache().get(cacheKey);
        if (mask != null) {
            return mask;
        }

        Image roundMask = Image.createImage(size, size, 0xff000000);
        Graphics gr = roundMask.getGraphics();
        gr.setColor(0xffffff);
        gr.setAntiAliased(true);
        gr.fillArc(0, 0, size, size, 0, 360);
        mask = roundMask.createMask();
        UI.getCache().set(cacheKey, mask);
        return mask;
    }

    public static Image createImageToStorage(String url, EncodedImage placeholder, String storageFile, URLImage.ImageAdapter adapter) {

        if (url == null || url.length() == 0) {
            return placeholder;
        }

        if (url.indexOf(" ") > 0) {
            url = url.substring(0, url.indexOf(" "));
        }
        if (storageFile == null) {
            storageFile = url + "@"+placeholder.getWidth()+"x"+placeholder.getHeight();
        } else if (storageFile.indexOf("@") == 0) {
            storageFile = url + storageFile;
        }
        return URLImage.createToStorage(placeholder, storageFile, url, adapter);



    }
    
}
