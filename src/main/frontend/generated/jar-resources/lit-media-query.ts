import {html, LitElement} from 'lit';
import {customElement, property, state} from 'lit/decorators.js';

@customElement('lit-media-query')
export class LitMediaQuery extends LitElement {

    @property()
    public query: string = '(max-width:460px)';

    @state()
    private _match: Boolean = false;

    private boundResizeHandler: () => void;

    constructor() {
        super();
        this.boundResizeHandler = this._handleResize.bind(this);
    }

    render() {
        return html`
            <style>
                :host {
                    display: none;
                }
            </style>
        `;
    }

    firstUpdated() {
        // Check media query once before 'resize' event
        this._initialMediaQueryCheck();
    }

    connectedCallback() {
        super.connectedCallback();
        // Check if Visual Viewport API is supported
        if (typeof window.visualViewport !== 'undefined' && window.visualViewport !== null) {
            window.visualViewport.addEventListener('resize', this.boundResizeHandler);
        } else {
            window.addEventListener('resize', this.boundResizeHandler);
        }
    }

    disconnectedCallback() {
        // Remove event listeners
        if (typeof window.visualViewport !== 'undefined' && window.visualViewport !== null) {
            window.visualViewport.removeEventListener(
                'resize',
                this.boundResizeHandler
            );
        } else {
            window.removeEventListener('resize', this.boundResizeHandler);
        }
        super.disconnectedCallback();
    }

    _initialMediaQueryCheck() {
        if (window.matchMedia(this.query).matches) {
            this.dispatchEvent(
                new CustomEvent('changed', {
                    detail: {
                        value: true
                    },
                    composed: true,
                    bubbles: true
                })
            );
        } else {
            this.dispatchEvent(
                new CustomEvent('changed', {
                    detail: {
                        value: false
                    },
                    composed: true,
                    bubbles: true
                })
            );
        }
    }

    _handleResize() {
        console.log("test");
        if (window.matchMedia(this.query).matches) {
            // From no match to match
            if (this._match === false) {
                this.dispatchEvent(
                    new CustomEvent('changed', {
                        detail: {
                            value: true
                        },
                        composed: true,
                        bubbles: true
                    })
                );
                this._match = true;
            }
        } else {
            // From match to no match
            if (this._match === true) {
                this.dispatchEvent(
                    new CustomEvent('changed', {
                        detail: {
                            value: false
                        },
                        composed: true,
                        bubbles: true
                    })
                );
                this._match = false;
            }
        }
    }
}
