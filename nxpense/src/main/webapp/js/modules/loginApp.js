(function(angular) {
  'use strict';

  var loginAppModule = angular.module('loginApp', []);

  // Controller to manage flippable containers
  loginAppModule.controller('welcomeController', ['$scope', function($scope) {
    $scope.visiblePanel = 'login';

    $scope.goToRegistration = function() {
      $scope.visiblePanel = 'registration';
    };

    $scope.goToLogin = function() {
      $scope.visiblePanel = 'login';
    }

  }]);
  // todo: replace hardcoded '/nxpense' path with computed web context
  loginAppModule.controller('loginController', ['$scope', '$http', '$location', function($scope, $http, $location) {
    $scope.email;
    $scope.password;
    $scope.rememberMe;

    $scope.login = function() {
      var rememberMe = this.rememberMe || false;
      var request = {
        method: 'POST',
        url: '/nxpense/login',
        params: {
          email: this.email,
          password: this.password,
          remember_me: rememberMe
        }
      };

      $http(request).success(function(data, status, headers, config) {
        var protocol = $location.protocol();
        var host = $location.host();
        var port = $location.port();
        var redirectUrl = protocol + '://' + host + ':' + port + data;

        // custom header sent by server to notify the client of a login failure
        var loginErrorMessage = headers('nxpense-login-error');

        if (loginErrorMessage) {
          $scope.loginForm.email.$setValidity('credentials', false);
        } else if (data) {
          // successful login -> redirect to URL sent by server
          window.location.assign(redirectUrl);
        }
      });
    };

    // When any of credentials changes, reset previous error
    $scope.$watch('email', function() {
      $scope.loginForm.email.$setValidity('credentials', true);
    });

    $scope.$watch('password', function() {
      $scope.loginForm.email.$setValidity('credentials', true);
    });
  }]);

  // Controller to manage registration process
  loginAppModule.controller('registrationController', ['$scope', '$http', function($scope, $http) {
    var emailTakenRule = 'emailTaken';
    $scope.email;
    $scope.password;
    $scope.passwordRepeat;

    $scope.createAccount = function() {
      var request = {
        method: 'POST',
        url: '/nxpense/account/new',
        params: {
          email: this.email,
          password: this.password,
          passwordRepeat: this.passwordRepeat
        },

        cache: false
      };

      var response = $http(request);

      response.success(function(data, status, headers, config) {
        // programmatic login -> redirect to URL sent by server
        window.location.assign(data);
      });

      response.error(function(data, status) {
        if (status === 409) {
          $scope.registrationForm.email.$setValidity(emailTakenRule, false);
        }
      });
    }

    // When passwordRepeat field changes, check if it matches password
    $scope.$watch('passwordRepeat', function(newValue, oldValue, scope) {
      var validationRule = 'confirmationCheck';

      // Custom validation rule: comparison of repeated password with chosen password if the
      // latter is not 'undefined'
      if (scope.password && scope.password !== newValue) {
        scope.registrationForm.passwordRepeat.$setValidity(validationRule, false);
      } else {
        scope.registrationForm.passwordRepeat.$setValidity(validationRule, true);
      }
    });

    // When email field changes, if there is a previous "email taken" message, cleanse it
    $scope.$watch('email', function(newValue, oldValue, scope) {
      var isEmailTaken = scope.registrationForm.email.$error[emailTakenRule];

      if (isEmailTaken) {
        scope.registrationForm.email.$setValidity(emailTakenRule, true);
      }
    });
  }]);
})(window.angular);