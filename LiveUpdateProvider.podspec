Pod::Spec.new do |s|
  s.name             = 'LiveUpdateProvider'
  s.version          = '0.2.0'
  s.summary          = 'Native contracts for live update providers.'
  s.homepage         = 'https://github.com/ionic-team/live-update-provider-sdk'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'Ionic Team' => 'hi@ionic.io' }
  s.source           = { :git => 'https://github.com/ionic-team/live-update-provider-sdk.git', :tag => s.version.to_s }

  s.ios.deployment_target = '15.0'
  s.swift_version    = '5.9'
  s.source_files     = 'ios/Sources/LiveUpdateProvider/**/*'
end
