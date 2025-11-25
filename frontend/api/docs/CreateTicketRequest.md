
# CreateTicketRequest


## Properties

Name | Type
------------ | -------------
`title` | string
`description` | string
`assigneeUsername` | string
`articleId` | number

## Example

```typescript
import type { CreateTicketRequest } from ''

// TODO: Update the object below with actual values
const example = {
  "title": null,
  "description": null,
  "assigneeUsername": null,
  "articleId": null,
} satisfies CreateTicketRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CreateTicketRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


