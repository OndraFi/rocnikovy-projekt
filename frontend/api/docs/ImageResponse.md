
# ImageResponse


## Properties

Name | Type
------------ | -------------
`id` | number
`filename` | string
`originalFilename` | string
`contentType` | string
`fileSize` | number
`uploadedAt` | Date
`url` | string

## Example

```typescript
import type { ImageResponse } from ''

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "filename": null,
  "originalFilename": null,
  "contentType": null,
  "fileSize": null,
  "uploadedAt": null,
  "url": null,
} satisfies ImageResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ImageResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


