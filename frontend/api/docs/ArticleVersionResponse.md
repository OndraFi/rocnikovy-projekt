
# ArticleVersionResponse


## Properties

Name | Type
------------ | -------------
`id` | number
`versionNumber` | number
`createdAt` | Date
`createdBy` | [UserResponse](UserResponse.md)
`content` | string

## Example

```typescript
import type { ArticleVersionResponse } from ''

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "versionNumber": null,
  "createdAt": null,
  "createdBy": null,
  "content": null,
} satisfies ArticleVersionResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ArticleVersionResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


