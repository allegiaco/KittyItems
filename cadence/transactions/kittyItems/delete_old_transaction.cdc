import KittyItems from 0xd133c13c48f8859c


transaction {

  prepare(acct: AuthAccount) {

    let oldCollection <- acct.load<@KittyItems.Collection>(from: KittyItems.CollectionStoragePath)

    destroy oldCollection

  }

  execute {
  }
}