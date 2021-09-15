#import "FlutterLeboPlugin.h"
#if __has_include(<flutter_lebo/flutter_lebo-Swift.h>)
#import <flutter_lebo/flutter_lebo-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_lebo-Swift.h"
#endif

@implementation FlutterLeboPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterLeboPlugin registerWithRegistrar:registrar];
}
@end
