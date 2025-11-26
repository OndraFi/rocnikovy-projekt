
# PaginatedArticleResponse


## Properties

Name | Type
------------ | -------------
`articles` | [Array&lt;ArticleResponse&gt;](ArticleResponse.md)
`page` | number
`size` | number
`totalElements` | number
`totalPages` | number

## Example

```typescript
import type { PaginatedArticleResponse } from ''

// TODO: Update the object below with actual values
const example = {
  "articles": null,
  "page": null,
  "size": null,
  "totalElements": null,
  "totalPages": null,
} satisfies PaginatedArticleResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as PaginatedArticleResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


