console.log("this is the javascript");

const toggleSidebar = () => {
    if ($(".sidebar").is(":visible")) {

        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    } else {

        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};

const search = () => {
    // console.log("searching...")
    let query = $("#search-input").val();

    console.log(query);

    if (query == "") {
        $(".search-result").hide();
    } else {

        // sending request to server
        let url = `http://localhost:9090/search/${query}`;

        fetch(url)
            .then((response) => {
                return response.json();
            }).then((data) => {

                let text = `<div class='list-group'>`;

                data.forEach((contact) => {
                    text += `<a href="/user/contact/${contact.cid}" class='list-group-item list-group-action'> ${contact.name} </a>`
                })

                text += `</div>`;

                $(".search-result").html(text);
                $(".search-result").show();
            });
    }
};

// first request to order - to create server 

const paymentStart = () => {
    console.log("Payment Started...")
    let amount = $('#payment_field').val();
    console.log(amount);

    if (amount == '' || amount == null) {
        alert('amount is required');
        return;
    }
    console.log("Hellooo");
    // we will use ajax to send request to server to create order   
    $.ajax(
        {
            type: 'POST',
            url: '/user/create_order',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({ amount: amount, info: "order_request" }),
            success: function (response) {
                // invoke when success
                console.log(response);
                if (response.status == "created") {
                    // open payment form
                    var options = {
                        "key": "rzp_test_6jqRVfvybOgQ7q", // Enter the Key ID generated from the Dashboard
                        "amount": response.amount, // Amount is in currency subunits. Default currency is INR. Hence, 50000 refers to 50000 paise
                        "currency": "INR",
                        "name": "Smart Contact Manager",
                        "description": "Donation",
                        "image": "",
                        "order_id": response.order_id, //This is a sample Order ID. Pass the `id` obtained in the response of Step 1
                        "handler": function (response) {
                            console.log(response.razorpay_payment_id);
                            console.log(response.razorpay_order_id);
                            console.log(response.razorpay_signature)
                            console.log("Payment successful")
                            alert("Congrats.. Payment Successful !!")
                        },
                        "prefill": {
                            "name": "",
                            "email": "",
                            "contact": ""
                        },
                        "notes": {
                            "address": "SCM @ Shivam"
                        },
                        "theme": {
                            "color": "#3399cc"
                        }
                    };
                    var razorpay = new Razorpay(options);
                    razorpay.on('payment.failed', function (response) {
                        console.log(response.error.code);
                        console.log(response.error.description);
                        console.log(response.error.source);
                        console.log(response.error.step);
                        console.log(response.error.reason);
                        console.log(response.error.metadata.order_id);
                        console.log(response.error.metadata.payment_id);
                        alert("Oops !! Payment Failed");
                    });
                    razorpay.open();
                }
            },
            error: function (error) {
                console.log(error);
                alert('Something went wrong !!');
            }
        }
    )
};
