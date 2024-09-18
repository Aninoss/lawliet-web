import { LitElement, html } from 'lit';
import elementMatchMedia from 'element-match-media';


class ElementMediaQuery extends LitElement {
    static get properties() {
        return {
            /**
             * The CSS media query to evaluate.
             */
            query: {
                type: String
            },

            querymatches: {
                type: Boolean
            },
            /**
             * The element to which the styles should be applied
             */
            element: {
                type: Object
            }
        };
    }

    static get is() { return 'element-media-query'; }

    constructor() {
        super();
    }
    setElement(element) {
        if (this.element) {
            this._observer.unobserve(this.element);
        }
        this.element = element;
        if (this._observer === undefined) {
            this._observer = new ResizeObserver(entries => {
                window.requestAnimationFrame(() => {
                    if (!Array.isArray(entries) || !entries.length) {
                        return;
                    }
                    for (const entry of entries) {
                        // Custom event works for both node.onresize and node.addEventListener('resize') cases.
                        const evt = new CustomEvent('resize', {detail: entry, bubbles: false})
                        entry.target.dispatchEvent(evt);
                    }
                });
            });
        }
        element.addEventListener('resize', e => {
            const matches = elementMatchMedia(this.element, this.query).matches;
            if (this.querymatches !== matches) {
                this.querymatches = matches;
                this.$server.querymatchesChanged(this.querymatches);
            }
        });
    }

    setQuery(query) {
        this.query = query;
    }

    connectedCallback() {
        super.connectedCallback();
        if (this.element && this._observer) {
            this._observer.observe(this.element);
        }
        this.querymatches = elementMatchMedia(this.element, this.query).matches;
        this.$server.querymatchesChanged(this.querymatches);
    }

    disconnectedCallback() {
        super.disconnectedCallback();
        if (this.element && this._observer) {
            this._observer.unobserve(this.element);
        }
    }
}

customElements.define(ElementMediaQuery.is, ElementMediaQuery);