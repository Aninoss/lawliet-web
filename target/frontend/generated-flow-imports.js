const div = document.createElement('div');
div.innerHTML = '<custom-style><style include="lumo-color lumo-typography"></style></custom-style>';
document.head.insertBefore(div.firstElementChild, document.head.firstChild);
document.documentElement.setAttribute('theme', 'dark');

function addCssBlock(block) {
 const tpl = document.createElement('template');
 tpl.innerHTML = block;
 document.head.appendChild(tpl.content);
}
import $css_0 from 'Frontend/styles/commands.css';
addCssBlock(`<custom-style><style>${$css_0}</style></custom-style>`);
import $css_1 from 'Frontend/styles/styles.css';
addCssBlock(`<custom-style><style>${$css_1}</style></custom-style>`);
import $css_2 from 'Frontend/styles/styles-reversed.css';
addCssBlock(`<custom-style><style>${$css_2}</style></custom-style>`);
import $css_3 from 'Frontend/styles/bootstrap.css';
addCssBlock(`<custom-style><style>${$css_3}</style></custom-style>`);
import $css_4 from 'Frontend/styles/main.css';
addCssBlock(`<custom-style><style>${$css_4}</style></custom-style>`);
import $css_5 from 'Frontend/styles/home.css';
addCssBlock(`<custom-style><style>${$css_5}</style></custom-style>`);
import $css_6 from 'Frontend/styles/featurerequests.css';
addCssBlock(`<custom-style><style>${$css_6}</style></custom-style>`);
import $css_7 from 'Frontend/styles/charts.css';
addCssBlock(`<dom-module id="flow_css_mod_7" theme-for="vaadin-chart"><template><style include="vaadin-chart-default-theme">${$css_7}</style></template></dom-module>`);
import $css_8 from 'Frontend/styles/dashboard.css';
addCssBlock(`<custom-style><style>${$css_8}</style></custom-style>`);

import '@vaadin/flow-frontend/client-media-query.js';
import '@vaadin/flow-frontend/com/github/appreciated/grid-layout/grid-layout.js';
import '@vaadin/flow-frontend/flow-component-renderer.js';
import '@vaadin/vaadin-accordion/theme/lumo/vaadin-accordion.js';
import '@vaadin/vaadin-button/theme/lumo/vaadin-button.js';
import '@vaadin/vaadin-charts/vaadin-chart.js';
import '@vaadin/vaadin-checkbox/theme/lumo/vaadin-checkbox.js';
import '@vaadin/vaadin-details/theme/lumo/vaadin-details.js';
import '@vaadin/vaadin-icons/vaadin-icons.js';
import '@vaadin/vaadin-lumo-styles/color.js';
import '@vaadin/vaadin-lumo-styles/icons.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/typography.js';
import '@vaadin/vaadin-notification/theme/lumo/vaadin-notification.js';
import '@vaadin/vaadin-ordered-layout/theme/lumo/vaadin-horizontal-layout.js';
import '@vaadin/vaadin-ordered-layout/theme/lumo/vaadin-vertical-layout.js';
import '@vaadin/vaadin-select/theme/lumo/vaadin-select.js';
import '@vaadin/vaadin-tabs/theme/lumo/vaadin-tab.js';
import '@vaadin/vaadin-tabs/theme/lumo/vaadin-tabs.js';
import '@vaadin/vaadin-text-field/theme/lumo/vaadin-text-area.js';
import '@vaadin/vaadin-text-field/theme/lumo/vaadin-text-field.js';
import '@vaadin/flow-frontend/selectConnector.js';
var scripts = document.getElementsByTagName('script');
var thisScript;
var elements = document.getElementsByTagName('script');
for (var i = 0; i < elements.length; i++) {
    var script = elements[i];
    if (script.getAttribute('type')=='module' && script.getAttribute('data-app-id') && !script['vaadin-bundle']) {
        thisScript = script;break;
     }
}
if (!thisScript) {
    throw new Error('Could not find the bundle script to identify the application id');
}
thisScript['vaadin-bundle'] = true;
if (!window.Vaadin.Flow.fallbacks) { window.Vaadin.Flow.fallbacks={}; }
var fallbacks = window.Vaadin.Flow.fallbacks;
fallbacks[thisScript.getAttribute('data-app-id')] = {}
fallbacks[thisScript.getAttribute('data-app-id')].loadFallback = function loadFallback(){
   return import('./generated-flow-imports-fallback.js');
}