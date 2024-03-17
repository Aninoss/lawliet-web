var classUse = 'show';

function verticalBarSwitch() {
    var elements = document.getElementsByClassName('expandable');
    var toggleElement = document.getElementById('menu-toggle');
    for (var i = 0; i < elements.length; i++) {
        var element = elements[i];
        if (element.classList.contains(classUse)) {
            element.classList.remove(classUse);
            if (i === 0) toggleElement.setAttribute("theme", "icon");
        } else {
            element.classList.add(classUse);
            if (i === 0) toggleElement.setAttribute("theme", "icon primary");
        }
    }
}

function verticalBarHide() {
    var elements = document.getElementsByClassName('expandable');
    var toggleElement = document.getElementById('menu-toggle');
    for (var i = 0; i < elements.length; i++) {
        var element = elements[i];
        if (element.classList.contains(classUse)) {
            element.classList.remove(classUse);
            if (i === 0) toggleElement.setAttribute("theme", "icon");
        }
    }
}

function onLoad() {
    document.getElementById("loading-div").remove();
    document.getElementsByTagName("Main")[0].classList.add("fadein-class");
}

function closeCookieConsent(name, value) {
    var cookieDialog = document.getElementById("cookie-consent");
    cookieDialog.style.transform = "translateY(calc(100% + 32px))";
    createCookie(name, value, 400);
}

function createCookie(name, value, days) {
    var date, expires;
    if (days) {
        date = new Date();
        date.setTime(date.getTime()+(days*24*60*60*1000));
        expires = "; expires="+date.toGMTString();
    } else {
        expires = "";
    }
    document.cookie = name+"="+value+expires+"; path=/; secure";
}

function onScroll() {
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
        document.getElementById("header-out").classList.add("scroll-class");
    } else {
        document.getElementById("header-out").classList.remove("scroll-class");
    }
}

function scrollToTop() {
    window.scrollTo(0, 250);
}

function loadScript(url, callback)
{
    // Adding the script tag to the head as suggested before
    var head = document.head;
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;

    // Then bind the event to the callback function.
    // There are several events for cross browser compatibility.
    script.onreadystatechange = callback;
    script.onload = callback;

    // Fire the loading
    head.appendChild(script);
}

function openPaddle(environment, vendor, planId, quantity, locale, passthrough) {
    loadScript("https://cdn.paddle.com/paddle/paddle.js", () => {
        if (environment === "sandbox") {
            Paddle.Environment.set(environment);
        }
        Paddle.Setup({
            vendor: vendor,
            eventCallback: function(eventData) {
                if (eventData.event === "Checkout.Complete") {
                    const checkoutId = eventData.eventData.checkout.id;
                    window.location.href = "https://" + window.location.hostname + "/premium?paddle=" + checkoutId;
                }
            }
        });
        Paddle.Checkout.open({
            product: planId,
            quantity: quantity,
            locale: locale,
            passthrough: passthrough
        });
    });
}

function openPaddleBilling(environment, clientToken, priceId, locale, discordId, discordTag, discordAvatar) {
    loadScript("https://cdn.paddle.com/paddle/v2/paddle.js", () => {
        if (environment === "sandbox") {
            Paddle.Environment.set(environment);
        }
        Paddle.Setup({
            token: clientToken,
            checkout: {
                settings: {
                    locale: locale
                }
            },
            eventCallback: function (eventData) {
                if (eventData.name === "checkout.completed") {
                    const transactionId = eventData.data.transaction_id;
                    window.location.href = "https://" + window.location.hostname + "/premium?paddle_billing=" + transactionId;
                }
            }
        });
        Paddle.Checkout.open({
            items: [{
                priceId: priceId
            }],
            customData: {
                discordId: discordId,
                discordTag: discordTag,
                discordAvatar: discordAvatar
            }
        });
    });
}

function openPaddleCustom(environment, vendor, id) {
    loadScript("https://cdn.paddle.com/paddle/paddle.js", () => {
        let subdomainPrefix = "";
        if (environment === "sandbox") {
            Paddle.Environment.set(environment);
            subdomainPrefix = "sandbox-";
        }
        Paddle.Setup({
            vendor: vendor,
            eventCallback: function (eventData) {
                if (eventData.event === "Checkout.Complete") {
                    const checkoutId = eventData.eventData.checkout.id;
                    window.location.href = "https://" + window.location.hostname + "/premium?paddle=" + checkoutId;
                }
            }
        });
        Paddle.Checkout.open({
            override: 'https://' + subdomainPrefix + 'create-checkout.paddle.com/checkout/custom/' + id
        });
    });
}

function scrollToElement(elementId) {
    const element = document.getElementById(elementId);
    element.scrollIntoView();

    const headerBox = document.querySelector('#header-out');
    window.scrollBy(0, -headerBox.offsetHeight);
}