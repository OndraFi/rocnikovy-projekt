
# CreateArticleRequest


## Properties

Name | Type
------------ | -------------
`title` | string
`articleState` | string
`publishedAt` | Date
`content` | string
`categoryIds` | Set&lt;number&gt;
`editorUsername` | string

## Example

```typescript
import type { CreateArticleRequest } from ''

// TODO: Update the object below with actual values
const example = {
  "title": null,
  "articleState": null,
  "publishedAt": null,
  "content": null,
  "categoryIds": null,
  "editorUsername": null,
} satisfies CreateArticleRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CreateArticleRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


