var app = angular.module('ionicApp', ['ionic']);

app.controller('AppCtrl', function($scope) {
    ionic.Platform.ready(function() {
        navigator.splashscreen.hide();
    });

    $scope.amountAvailable = false;
    $scope.totalAvailable = false;
    $scope.amountPerPersonAvailable = false;

    $scope.calculateTip = function() {
        var getFixedTip = function(amount, pct) {
            return (amount * pct).toFixed(2);
        };

        $scope.amountAvailable = true;
        var amount = parseFloat(this.amount);
        $scope.pct10 = getFixedTip(amount, 0.1);
        $scope.pct15 = getFixedTip(amount, 0.15);
        $scope.pct20 = getFixedTip(amount, 0.2);
    };

    $scope.selectTipPct = function(pct) {
        var setTotal = function(amount, value) {
            $scope.totalAmount = (parseFloat(amount) + parseFloat(value)).toFixed(2);
        };

        if (10 === pct) {
            setTotal(this.amount, $scope.pct10);
        } else if (15 === pct) {
            setTotal(this.amount, $scope.pct15);
        } else if (20 === pct) {
            setTotal(this.amount, $scope.pct20);
        }
        $scope.selectedTip = pct;
        $scope.totalAvailable = true;
    };

    $scope.divideBill = function() {
        $scope.amountPerPerson = ($scope.totalAmount / parseInt(this.divideBy)).toFixed(2);
        $scope.amountPerPersonAvailable = true;
    };
});