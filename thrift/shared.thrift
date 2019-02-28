

/**
 * This Thrift file can be included by other Thrift files that want to share
 * these definitions.
 */

namespace cpp shared
namespace d share // "shared" would collide with the eponymous D keyword.
namespace dart shared
namespace java shared
namespace perl shared
namespace php shared
namespace haxe shared
namespace netcore shared

struct SharedStruct {
  1: i32 key
  2: string value
}

service SharedService {
  SharedStruct getStruct(1: i32 key)
}