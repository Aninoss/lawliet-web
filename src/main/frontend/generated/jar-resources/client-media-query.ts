import {html, LitElement} from 'lit';
import {customElement, property, state} from 'lit/decorators.js';
import './lit-media-query.ts';

@customElement('client-media-query')
export class ClientMediaQuery extends LitElement {

    @property()
    public query:string = '(max-width:460px)';

    @property()
    public queryCss:string = '(max-width:460px)';

    @state()
    private element?:HTMLElement;

    @state()
    private querymatches: boolean = false;

    constructor() {
        super();
    }

    render() {
        return html`
            <lit-media-query query=${this.query} @changed=${(e :CustomEvent) => this.tryApplyQueryCss(e.detail.value)}></lit-media-query>
        `;
    }

    tryApplyQueryCss(querymatches:boolean){
        this.querymatches = querymatches;
        if (this.querymatches && this.queryCss != null && this.element != null) {
            var mediaQueryCss = JSON.parse(this.queryCss);
            for (const x in mediaQueryCss) {
                // @ts-ignore
                this.element.style[x] = mediaQueryCss[x];
            }
        }
    }

    setElement(element:HTMLElement) {
        this.element = element;
        if (this.querymatches){
            this.tryApplyQueryCss(this.querymatches);
        }
    }

}