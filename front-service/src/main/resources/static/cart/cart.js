angular.module('market-front').controller('cartController', function ($scope, $http, $location, $localStorage) {
    const contextPathCore = 'http://localhost:5555/core/';
    const contextPathCart = 'http://localhost:5555/cart/';

    $scope.loadCart = function () {
        $http({
            url: contextPathCart + 'api/v1/cart/' + $localStorage.springWebGuestCartId,
            method: 'GET'
        }).then(function (response) {
            $scope.cart = response.data;
        });
    };

    $scope.disabledCheckOut = function () {
        alert("Для оформления заказа необходимо войти в учетную запись");
    }

    $scope.clearCart = function () {
        $http.get(contextPathCart + 'api/v1/cart/' + $localStorage.springWebGuestCartId + '/clear')
            .then(function (response) {
                $scope.loadCart();
            });
    }

    $scope.checkOut = function () {
        $http({
            url: contextPathCore + 'api/v1/orders',
            method: 'POST',
            data: $scope.orderDetails
        }).then(function (response) {
            $scope.clearCart();
            $scope.orderDetails = null
        }).catch(function (error) {
           console.error(error);
        })
    };

    $scope.loadCart();
});
