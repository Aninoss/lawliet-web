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

function updatePaddlePrices(data) {
    var currencyLabels = document.querySelectorAll(".paddle-currency");
    var quantity = data.eventData.product.quantity;
    var name = data.eventData.product.name;
    var subtotal = data.eventData.checkout.prices.customer.total - data.eventData.checkout.prices.customer.total_tax;

    if (quantity > 1) {
        name = quantity + "x " + name
    }
    for(var i = 0; i < currencyLabels.length; i++) {
        currencyLabels[i].innerHTML = data.eventData.checkout.prices.customer.currency + " ";
    }

    document.getElementById("paddle-title").innerHTML = name;
    document.getElementById("paddle-subtotal").innerHTML = subtotal.toFixed(2);
    document.getElementById("paddle-tax").innerHTML = data.eventData.checkout.prices.customer.total_tax;
    document.getElementById("paddle-total").innerHTML = data.eventData.checkout.prices.customer.total;
}

function openPaddle(vendor, planId, quantity, locale, passthrough) {
    Paddle.Environment.set('sandbox');
    Paddle.Setup({
        vendor: vendor,
        eventCallback: function(eventData) {
            if (eventData.event === "Checkout.Complete") {
                var checkoutId = eventData.eventData.checkout.id;
                window.location.href = "https://localhost:8443/premium?paddle=" + checkoutId; //TODO
            }
            updatePaddlePrices(eventData);
        }
    });
    Paddle.Checkout.open({
        method: 'inline',
        product: planId,
        quantity: quantity,
        disableLogout: true,
        locale: locale,
        passthrough: passthrough,
        frameTarget: 'paddle-container',
        frameStyle: 'width:100%; min-width:312px; background-color: transparent; border: none;'
    });
}