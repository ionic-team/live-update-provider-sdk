Pod::Spec.new do |s|
  s.name             = 'LiveUpdateProvider'
  s.version          = '0.1.0'
  s.summary          = 'Interface and models for Live Update Providers.'
  s.homepage         = 'https://github.com/ionic-team/live-update-provider-sdk'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'Ionic Team' => 'hi@ionicframework.com' }
  s.source           = { :git => 'https://github.com/ionic-team/live-update-provider-sdk.git', :tag => s.version.to_s }

  s.ios.deployment_target = '13.0'
  s.swift_version    = '5.0'
  s.source_files     = 'ios/LiveUpdateProvider/Sources/LiveUpdateProvider/**/*'
end
