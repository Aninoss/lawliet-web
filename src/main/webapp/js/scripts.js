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
    createCookie(name, value, 30);
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

function openPaddle(vendor, planId, quantity, locale, passthrough) {
    Paddle.Setup({
        vendor: vendor,
        eventCallback: function(eventData) {
            if (eventData.event === "Checkout.Complete") {
                const checkoutId = eventData.eventData.checkout.id;
                window.location.href = "https://lawlietbot.xyz/premium?paddle=" + checkoutId;
            }
        }
    });
    Paddle.Checkout.open({
        product: planId,
        quantity: quantity,
        locale: locale,
        passthrough: passthrough
    });
}

function openPaddleCustom(vendor, id) {
    Paddle.Setup({
        vendor: vendor,
        eventCallback: function(eventData) {
            if (eventData.event === "Checkout.Complete") {
                const checkoutId = eventData.eventData.checkout.id;
                window.location.href = "https://lawlietbot.xyz/premium?paddle=" + checkoutId;
            }
        }
    });
    Paddle.Checkout.open({
        override: 'https://create-checkout.paddle.com/checkout/custom/' + id
    });
}

function scrollToElement(elementId) {
    const element = document.getElementById(elementId);
    element.scrollIntoView();

    const headerBox = document.querySelector('#header-out');
    window.scrollBy(0, -headerBox.offsetHeight);
}