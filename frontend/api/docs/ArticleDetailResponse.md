
# ArticleDetailResponse


## Properties

Name | Type
------------ | -------------
`id` | number
`title` | string
`articleState` | string
`publishedAt` | Date
`content` | string
`currentVersion` | number
`author` | [UserResponse](UserResponse.md)
`editor` | [UserResponse](UserResponse.md)
`categories` | [Set&lt;CategoryResponse&gt;](CategoryResponse.md)

## Example

```typescript
import type { ArticleDetailResponse } from ''

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "title": null,
  "articleState": null,
  "publishedAt": null,
  "content": null,
  "currentVersion": null,
  "author": null,
  "editor": null,
  "categories": null,
} satisfies ArticleDetailResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ArticleDetailResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


